package com.bizmda.bizsip.dynamicconfig.config;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.watchers.DelayWatcher;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author 史正烨
 */
@Slf4j
public class ConfigWatchMonitor {
    public ConfigWatchMonitor(String serverAddr,String configPath) {
        Path path = Paths.get(configPath);

        WatchMonitor watchMonitor = null;
        try {
            watchMonitor = WatchMonitor.createAll(path, new DelayWatcher(new ConfigWatcher(serverAddr,configPath),1000));
            watchMonitor.setMaxDepth(7);
            watchMonitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (watchMonitor != null) {
                watchMonitor.close();
            }
        }

    }
}
