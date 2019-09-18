/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *
 * NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 * TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 * USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 * PERMISSION OF THALES.  
 */
package com.thales.ifec.service.ingestion.service;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.thales.ifec.service.ingestion.IngestionException;
import com.thales.ifec.service.ingestion.configuration.AmazonS3Client;
import com.thales.ifec.service.ingestion.configuration.FileStatus;
import com.thales.ifec.service.ingestion.configuration.Producer;
import com.thales.ifec.service.ingestion.configuration.SftpConfig.UploadGateway;
import com.thales.ifec.service.ingestion.configuration.AzureConfig;
import com.thales.ifec.service.ingestion.domain.OffloadType;
import com.thales.ifec.service.ingestion.domain.OffloadsMaster;
import com.thales.ifec.service.ingestion.domain.OffloadsMasterRepository;
import com.thales.ifec.service.ingestion.domain.OutputMessage;
import com.thales.ifec.service.ingestion.domain.RthmStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;

@Service
@Slf4j
public class FileService {

	@Autowired
	private AmazonS3Client client;

	@Autowired
	private Producer producer;

	@Autowired
	private OffloadsMasterRepository offloadRepo;

	@Autowired
	private UploadGateway uploadGateway;

	@Autowired
	private AzureConfig azureConfig;

	private static final String PREFIX_USAGE = "USAGE";

	private static final String PREFIX_TVPERFORMANCE = "TVPERF";

	private static final String PREFIX_BITE = "BITE";

	private static final String PREFIX_KALOGS = "Ka_United";

	private static final String PREFIX_CONNECTIVITY = "CONLOG";

	private static final String SOURCE = "Manual";

	private static final String DATEFORMAT_DATETIME = "yyyyMMddHHmmss";

	private static final String REGEX_VALID_DATETIME = "^[0-9]{4}(1[0-2]|0[1-9])(3[01]|[12][0-9]|0[1-9])([01]?[0-9]|2[0-3])[0-5][0-9][0-5][0-9]$";

	private static final String MSG_DATE_NOT_SAVED = "Offload date will not saved in the data base";

	private final Object lock = new Object();

	/**
	 * To process the uploaded file.
	 * 
	 * @param multipartFile file to process
	 * @return
	 */
	public OutputMessage uploadFile(MultipartFile multipartFile) {
		String fileName = "";
		File file = null;

		log.info("Received upload request for the file : {}", multipartFile.getOriginalFilename());
		try {
			file = convertMultiPartToFile(multipartFile);
			fileName = String.valueOf(file);
			String errorMsg = validateUploadFile(fileName);

			if (!StringUtils.isEmpty(errorMsg)) {
				log.warn("Invalid offload file, {}", errorMsg);
				return new OutputMessage(HttpStatus.BAD_REQUEST.value(), "Invalid offload file", errorMsg);
			}

			OffloadType offloadType = getOffloadType(fileName);

			// Check whether the file is already uploaded
			if (offloadRepo.findByFileName(fileName) != null) {
				log.error("Offload file '{}' has already been uploaded ", fileName);
				return new OutputMessage(HttpStatus.CONFLICT.value(), "File already exists",
						"File has already been uploaded, try different file");
			}

			if (offloadType == OffloadType.TVPERFORMANCE || offloadType == OffloadType.BITE) {

				// Handle BITE and TVPERF uploads
				processOffloadRequest(fileName, file, multipartFile.getSize(), offloadType);
			} else if (offloadType == OffloadType.KALOGS) {
				uploadKaLogsFileTos3bucket(fileName, file);
				insertFile(fileName, OffloadType.CONNECTIVITY, 0, null);
			}
			// Handle Usage uploads
			else if (offloadType == OffloadType.USAGE) {
				uploadFileToAzureBlobStorage(fileName, file);
				// insertFile(fileName, OffloadType.CONNECTIVITY, 0, null);
			} else {
				// Handle other uploads :: TBD
				return new OutputMessage(HttpStatus.BAD_REQUEST.value(), "Upload type not supported",
						"Please select USAGE, TVPERF, BITE or Ka_United file");
			}
		} catch (IngestionException ex) {
			return new OutputMessage(HttpStatus.CONFLICT.value(), "Failed to process offload request", ex.getMessage());
		} catch (Exception ex) {
			log.error("Exception occurred while uploading the fle :'{}'", fileName, ex);
			return new OutputMessage(HttpStatus.CONFLICT.value(), "Failed to process offload request", ex.getMessage());
		} finally {
			try {
				if (file != null && !Files.deleteIfExists(file.toPath())) {
					log.warn("Unable to delete multipart file, an error occurred while deleting the file {}",
							file.toPath());
				}
			} catch (IOException e) {
				log.warn("Unable to delete multipart file, an IOException occurred while deleting the file..");
			}
		}

		return new OutputMessage(HttpStatus.OK.value(), "Success", "File uploaded successfully");
	}

	/**
	 * Validation for input file (Empty, Filename format, TGZ, TVPERF validation
	 * done).
	 * 
	 * @param fileName off-load file name
	 * @return
	 */
	private String validateUploadFile(String fileName) {
		String errMsg = "";

		if (fileName.contains("..")) {
			errMsg = "Invalid file format";
		} else if (fileName.startsWith(PREFIX_KALOGS)) {
			if (!fileName.toLowerCase().contains(".zip")) {
				errMsg = "Upload zip files only";
			}
		} else if (fileName.toUpperCase().startsWith(PREFIX_TVPERFORMANCE)
				|| fileName.toUpperCase().startsWith(PREFIX_BITE) || fileName.toUpperCase().startsWith(PREFIX_USAGE)) {
			if (!fileName.toLowerCase().contains(".tgz")) {
				errMsg = "Upload tgz files only";
			}
		} else {
			errMsg = "Upload type not supported, please select USAGE or TVPERF or BITE or Ka_United file";
		}

		return errMsg;
	}

	/**
	 * Convert multi-part file to normal file.
	 * 
	 * @param file mutil-part file to process
	 * @throws IngestionException type of exception
	 */
	private File convertMultiPartToFile(MultipartFile file) throws IngestionException {
		File convFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convFile)) {
			fos.write(file.getBytes());
		} catch (IOException ioe) {
			throw new IngestionException("Exception occurred while reading the file", ioe);
		}

		return convFile;
	}

	/**
	 * Upload file to S3.
	 * 
	 * @param fileName off-load file name
	 * @param file     actual file to process
	 * @throws IngestionException type of exception to be thrown
	 * @throws IOException        type of exception to be thrown
	 */
	private void uploadKaLogsFileTos3bucket(String fileName, File file) {
		log.info("File is being prepared. Upload to S3 in-progress");

		client.getKaLogsClient().putObject(new PutObjectRequest(client.getKalogsbucketName(), fileName, file)
				.withCannedAcl(CannedAccessControlList.PublicRead));

		log.info("Successfully uploaded the file '{}' to S3 Bucket", fileName);
	}

	/**
	 * To process offload request.
	 * 
	 * @param fileName    off-load file name
	 * @param offloadFile file to process
	 * @param fileSize    size of the file
	 * @param offloadType type of offload
	 * @throws IngestionException type of exception to be thrown
	 */
	private void processOffloadRequest(String fileName, File offloadFile, long fileSize, OffloadType offloadType)
			throws IngestionException {

		RthmStatus rthmStatus = null;

		// Upload the file to S3 bucket
		uploadFileTos3bucket(fileName, offloadFile);

		// SFTP the BITE off-load file to legacy BITE tool server for processing
		if (offloadType == OffloadType.BITE) {
			try {
				Future<String> response = uploadGateway.uploadToBiteServer(offloadFile);

				if (response != null) {
					log.info("Successfully uploaded '{}' to {}", fileName, response.get());
				}
			} catch (Exception ex) {
				log.error("Exception occurred while uploading the BITE offload file to SFTP folder :'{}'", fileName,
						ex);

				String displayMsg = (ex.getCause() instanceof MessagingException)
						? ((MessagingException) ex.getCause()).getMostSpecificCause().getMessage()
						: ex.getMessage();
				throw new IngestionException(displayMsg);
			}

			// Get RTHM status
			rthmStatus = getRthmStatus(offloadFile);
		}

		// Save the off-load file meta data to health APP data base
		insertFile(fileName, offloadType, fileSize, rthmStatus);
		log.info("Filename inserted in database : '{}'", fileName);

		// Send a message to processing service
		producer.produceMsg(fileName, offloadType);
	}

	/**
	 * Uploads the file to S3 bucket.
	 * 
	 * @param fileName off-load file name
	 * @param file     file to be processed
	 */
	private void uploadFileTos3bucket(String fileName, File file) {
		log.info("File is being prepared. Upload to S3 in-progress");
		client.getClient()
				.putObject(new PutObjectRequest(client.getBucketName(),
						new StringBuilder("new/").append(fileName).toString(), file)
								.withCannedAcl(CannedAccessControlList.PublicRead));

		log.info("Successfully uploaded the file '{}' to S3 Bucket", fileName);
	}

	/**
	 * Upload file to Azure blob.
	 * 
	 * @param fileName off-load file name
	 * @param file     actual file to process
	 */
	private void uploadFileToAzureBlobStorage(String fileName, File file) {

		CloudStorageAccount storageAccount;
		CloudBlobClient blobClient = null;
		CloudBlobContainer container = null;

		try {
			// Parse the connection string and create a blob client to interact with Blob
			// storage
			log.info("File is being prepared. Upload to Azure blob in-progress");
			storageAccount = CloudStorageAccount.parse(azureConfig.getStorageConnectionString());
			blobClient = storageAccount.createCloudBlobClient();
			container = blobClient.getContainerReference(azureConfig.getStorageAccountName());

			// Create the container if it does not exist with public access.
			log.info("Creating container: " + container.getName());
			container.createIfNotExists(BlobContainerPublicAccessType.BLOB, new BlobRequestOptions(),
					new OperationContext());
			CloudBlockBlob blob = container.getBlockBlobReference(fileName);

			// Creating blob and uploading file to it
			blob.uploadFromFile(fileName);
			log.info("Successfully uploaded the file '{}' to Azure blob", fileName);
		} catch (StorageException ex) {
			log.error("Exception occurred while uploading the offload file to Azure blob", ex.getMessage());
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
	}

	/**
	 * Saves off-load meta data in health application data base.
	 * 
	 * @param fileName    off-load file name
	 * @param offloadType type of offload
	 * @param fileSize    size of off-load file
	 * @param rthmStatus  file rthm status
	 */
	public void insertFile(String fileName, OffloadType offloadType, long fileSize, RthmStatus rthmStatus)
			throws IngestionException {

		OffloadsMaster offloadMaster = new OffloadsMaster();

		offloadMaster.setFileName(fileName);
		offloadMaster.setStatus(
				(offloadType == OffloadType.BITE) ? FileStatus.NEW.getValue() : FileStatus.UPLOADED.getValue());

		offloadMaster.setRthmStatus(rthmStatus);
		offloadMaster.setUploadedTime(new Date());

		if (!offloadType.getType().equalsIgnoreCase("connectivity")) {
			offloadMaster.setOffloadDate(getOffloadDate(fileName));
		}

		// Set off-load type
		offloadMaster.setOffloadType(offloadType.getType());

		// Airline info will be updated while processing the off-load
		offloadMaster.setAirlineId(-1);
		offloadMaster.setFileSize(fileSize);
		offloadMaster.setOppFound(false);
		offloadMaster.setSource(SOURCE);

		synchronized (lock) {
			// Confirm whether the file has not already been saved in the database by a
			// duplicate request
			if (offloadRepo.findByFileName(fileName) != null) {
				log.error("Offload file '{}' is already saved in the databse by another request", fileName);
				throw new IngestionException(
						"Probable duplicate request, " + "Offload file has already been uploaded.");
			}

			// Save off load meta-data to data base
			offloadRepo.save(offloadMaster);
		}

	}

	/**
	 * To get rthm status of the file.
	 * 
	 * @param offloadFile file to be processed
	 * @return RthmStatus status
	 * 
	 * @throws IngestionException type of exception to be thrown
	 */
	private RthmStatus getRthmStatus(File offloadFile) throws IngestionException {

		try (GZIPInputStream gzipStream = new GZIPInputStream(new FileInputStream(offloadFile));
				TarArchiveInputStream tarStream = new TarArchiveInputStream(gzipStream);) {

			TarArchiveEntry entry;
			while ((entry = tarStream.getNextTarEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}
				String entryName = FilenameUtils.normalize(entry.getName());

				if (entryName.toUpperCase().matches("^(HMS_)[A-Z0-9\\-\\_]+.*\\.TGZ$")) {
					log.info("RTHM file {} available in BITE offload ...", entryName);
					return RthmStatus.UPLOADED;
				}
			}
		} catch (IOException ioe) {
			log.error("Exception occurred while checking for RTHM data availability:", ioe);
			throw new IngestionException("Exception occurred while extracting the Bite offload file ...");
		}
		return RthmStatus.NONE;
	}

	/**
	 * To get offload date using file name.
	 * 
	 * @param fileName off-load file name
	 * @return Date offload date
	 */
	private Date getOffloadDate(String fileName) {

		if (fileName.matches("^(TVPERF)_[0-9]{14}_[A-Z0-9\\-\\_]+\\.tgz")) {
			String dateString = fileName.split("_")[1];
			try {

				if (dateString.matches(REGEX_VALID_DATETIME)) {
					return new SimpleDateFormat(DATEFORMAT_DATETIME).parse(dateString);
				} else {
					log.error("Invalid date String : {}, {}", dateString, MSG_DATE_NOT_SAVED);
				}
			} catch (java.text.ParseException ex) {
				log.error("Unable to parse offload date : {}, {}", dateString, MSG_DATE_NOT_SAVED, ex);
			}
		}
		return null;
	}

	/**
	 * Returns the Off-load type based on the off-load file name.
	 * 
	 * @param fileName off-load file name
	 */
	private OffloadType getOffloadType(String fileName) {

		String[] offloadTypes = { PREFIX_BITE, PREFIX_TVPERFORMANCE, PREFIX_KALOGS, PREFIX_CONNECTIVITY, PREFIX_USAGE };

		Optional<String> fileNamePrefix = Stream.of(offloadTypes).filter(fileName::startsWith).findAny();

		if (fileNamePrefix.isPresent()) {

			switch (fileNamePrefix.get()) {

			case PREFIX_BITE:
				return OffloadType.BITE;

			case PREFIX_TVPERFORMANCE:
				return OffloadType.TVPERFORMANCE;

			case PREFIX_KALOGS:
				return OffloadType.KALOGS;

			case PREFIX_CONNECTIVITY:
				return OffloadType.CONNECTIVITY;

			case PREFIX_USAGE:
				return OffloadType.USAGE;

			default:
				return null;
			}
		} else {
			return null;
		}
	}

}