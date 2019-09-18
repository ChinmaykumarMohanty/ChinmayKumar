/*
 * Copyright 2018-2019 Thales and/or its affiliates. All rights reserved.
 *  
 *   NOTICE - THE INFORMATION CONTAINED HEREIN IS PROPRIETARY AND CONFIDENTIAL
 *   TO THALES AVIONICS, INC. (THALES) IN WHOLE OR IN PART AND SHALL NOT BE
 *   USED OR DISCLOSED IN WHOLE OR IN PART WITHOUT FIRST OBTAINING THE WRITTEN
 *   PERMISSION OF THALES.
 */

package com.thales.ifec.service.ingestion.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OffloadsMasterRepository extends CrudRepository<OffloadsMaster, Integer> {

  OffloadsMaster findByFileName(String filename);

}
