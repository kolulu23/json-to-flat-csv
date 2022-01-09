package org.kolulu.api;

import org.kolulu.ConverterMode;

/**
 * Convert json to csv.
 *
 * @author kolulu
 * <br/>Created at 2022/1/8 14:57
 */
public interface IConverter {
    /**
     * Supported converter mode.
     * This method provides a simple way to get the implementation detail.
     *
     * @return A list of supported converter modes.
     */
    ConverterMode[] getConverterMode();
}
