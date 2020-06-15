package framework.eva.orm.configuration;


import framework.eva.orm.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author artem
 * @since 29.10.17
 */
public class ServerProperties extends Properties {
    private static final Logger logger = LoggerFactory.getLogger(ServerProperties.class);
    private static final long serialVersionUID = 1L;

    public ServerProperties(final String configFile) {
        this(configFile, true);
    }

    public ServerProperties(final String configFile, final boolean msg) {

        if (msg)
            logger.info("Loading: " + configFile + ".");

        try (InputStream is = new FileInputStream(new File(configFile)); InputStreamReader reader = new InputStreamReader(is, "UTF-8")) {
            load(reader);
        } catch (final Exception e) {
            logger.warn("Configuration file '" + configFile + "' cannot be loaded!");
        }
    }

    @Override
    public String getProperty(final String name, final String defaultValue) {
        String value = getProperty(name);
        if (value == null) {
            logger.warn("Value for \"%s\" in file %s is not set, using default: %s.",name, defaultValue);
            value = defaultValue;
        }

        if (value != null)
            value = value.trim();
        return value;
    }

    public final long getTime(final String name, final TimeUnit timeUnit, final String defaultValue) {
        String value = getProperty(name);
        if (value == null) {
            logger.warn("Value for \"%s\" in file %s is not set, using default: %s.",name, defaultValue);
            value = defaultValue;
        }

        final int timeSeconds = parseTime(value, 0);
        final long result = timeUnit.convert(timeSeconds, TimeUnit.SECONDS);
        return result;
    }

    private static final int parseTime(final String value, final int defaultValue) {
        if (value == null) {
            return defaultValue;
        }

        if (isDigits(value)) {
            return Integer.parseInt(value);
        }

        int time = 0;

        int prevIndex = -1;
        for (final TimeParams t : TimeParams.VALUES) {
            final String search = t.toString().toLowerCase();
            final int index = prevIndex != -1 ? value.indexOf(search, prevIndex) : value.indexOf(search);
            if (index != -1) {
                final String v = prevIndex != -1 ? value.substring(prevIndex, index) : value.substring(0, index);
                prevIndex = index + t.length;
                final int number = Integer.parseInt(v);
                time += number * t.mod;
            }
        }

        return time;
    }

    public boolean getBoolean(final String name, final boolean defaultValue) {
        boolean val = defaultValue;
        String value;
        if ((value = getProperty(name, String.valueOf(defaultValue))) != null) {
            value = value.trim();
            val = Boolean.parseBoolean(value);
        }

        return val;
    }

    public int getInteger(final String name, final int defaultValue) {
        int val = defaultValue;
        String value;
        if ((value = getProperty(name, String.valueOf(defaultValue))) != null) {
            value = value.trim();
            val = Integer.parseInt(value);
        }

        return val;
    }

    public byte getByte(final String name, final int defaultValue) {
        int val = defaultValue;
        String value;
        if ((value = getProperty(name, String.valueOf(defaultValue))) != null) {
            value = value.trim();
            val = Byte.parseByte(value);
        }

        return (byte) val;
    }

    public long getLong(final String name, final long defaultValue) {
        long val = defaultValue;
        String value;
        if ((value = getProperty(name, String.valueOf(defaultValue))) != null) {
            value = value.trim();
            val = Long.parseLong(value);
        }

        return val;
    }

    public double getDouble(final String name, final double defaultValue) {
        double val = defaultValue;
        String value;
        if ((value = getProperty(name, String.valueOf(defaultValue))) != null) {
            value = value.trim();
            val = Double.parseDouble(value);
        }

        return val;
    }

    public float getFloat(final String name, final float defaultValue) {
        float val = defaultValue;
        String value;
        if ((value = getProperty(name, String.valueOf(defaultValue))) != null) {
            value = value.trim();
            val = Float.parseFloat(value);
        }

        return val;
    }

//    public final int[] getIntArray(final String name, final int... defaultValue) {
//        final String value = getProperty(name);
//        if (value == null) {
//            warning(name, defaultValue);
//            return defaultValue;
//        }
//        return ArraysUtil.toIntArray(value.trim());
//    }
//
//    public final long[] getLongArray(final String name, final long... defaultValue) {
//        final String value = getProperty(name);
//        if (value == null) {
//            warning(name, defaultValue);
//            return defaultValue;
//        }
//        return ArraysUtil.toLongArray(value.trim());
//    }
//
//    public final float[] getFloatArray(final String name, final float... defaultValue) {
//        final String value = getProperty(name);
//        if (value == null) {
//            warning(name, defaultValue);
//            return defaultValue;
//        }
//        return ArraysUtil.toFloatArray(value.trim());
//    }
//
//    public final double[] getDoubleArray(final String name, final double... defaultValue) {
//        final String value = getProperty(name);
//        if (value == null) {
//            warning(name, defaultValue);
//            return defaultValue;
//        }
//        return ArraysUtil.toDoubleArray(value.trim());
//    }
//
//    public final String[] getStringArray(final String name, final String... defaultValue) {
//        final String value = getProperty(name);
//        if (value == null) {
//            warning(name, (Object[]) defaultValue);
//            return defaultValue;
//        }
//        return ArraysUtil.toStringArray(value.trim());
//    }

//    private final void warning(final String var, final Object... defaultValue) {
//        if (showWarning)
//            logger.warn(String.format(log_format, new Object[]{var, configFile, ArraysUtil.deepToString(defaultValue)}));
//    }

    private enum TimeParams {
        DAY(86400, 3),
        HOUR(3600, 4),
        MIN(60, 3),
        SEC(1, 3);

        public static final TimeParams[] VALUES = values();

        public final int mod;
        public final int length;

        private TimeParams(final int mod, final int length) {
            this.mod = mod;
            this.length = length;
        }
    }

    private static boolean isDigits(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}






