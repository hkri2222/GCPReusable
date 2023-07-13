package com.sample.Utils;

import com.sample.steps.CustomBaseStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomUtilStep {
    private static final Logger logger  = LoggerFactory.getLogger(CustomBaseStep.class);

    static String filepath = FileConfig.getInstance().getStringConfigValue("datafiles.excelfilepath");
}
