package no.utgdev.lightdi.exampleapp;


import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class DBImplementation implements DBAbstraction {
    final static Logger logger = getLogger(DBImplementation.class);

    public DBImplementation() {
        logger.debug("DBImplementation constructor");
    }
}
