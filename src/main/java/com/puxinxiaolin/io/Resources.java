package com.puxinxiaolin.io;

import java.io.InputStream;

public class Resources {

    /**
     * 根据配置文件的路径，并把配置文件加载成字节输入流，存到内存中
     *
     * @param path
     * @return
     */
    public static InputStream getResourceAsStream(String path) {
        return Resources.class.getClassLoader()
                .getResourceAsStream(path);
    }

}
