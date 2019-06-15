/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.datatransfer.ui;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class HoerbuchkatalogUebertragenTask extends Service<Boolean> {

    @Override
    protected Task<Boolean> createTask() {
        return null;
    }

    private static class MyTask extends Task<Boolean> {

        @Override
        protected Boolean call() throws Exception {
            return null;
        }

    }

}
