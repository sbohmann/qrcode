
package at.yeoman.tools.qrCodeReader;

import java.io.File;
import java.util.prefs.Preferences;

class PreferenceStorage
{
    private static final String ApplicationId = "at.yeoman.tools.qrCodeReader";
    private static final String LastDirectoryKey = "lastDirectory";
    
    static synchronized File load()
    {
        String path = Preferences.userRoot().node(ApplicationId).get(LastDirectoryKey, null);
        if (path != null)
        {
            File result = new File(path);
            if (result.isDirectory())
            {
                return result;
            }
        }
        return null;
    }
    
    static synchronized void save(File lastDirectory)
    {
        try
        {
            if (lastDirectory != null)
            {
                Preferences.userRoot().node(ApplicationId).put(LastDirectoryKey, lastDirectory.getCanonicalPath());
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
}
