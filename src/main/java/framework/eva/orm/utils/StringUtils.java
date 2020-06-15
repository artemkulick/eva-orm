package framework.eva.orm.utils;

public class StringUtils
{
    public static boolean isEmpty(String s)
    {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(String s)
    {
        return s != null && !s.isEmpty();
    }

    public static String trimToNull(String string)
    {
        if (string != null)
        {
            string = string.trim();
            if (string.length() == 0)
            {
                string = null;
            }
        }
        return string;
    }
}
