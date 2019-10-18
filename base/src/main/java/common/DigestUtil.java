package common;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;

/**
 * 描述：返回 digest 加密内容
 * 作者：liangyongtong
 * 日期：2019/10/17 4:54 PM
 * 类名：DigestUtil
 * 版本： version 1.0
 */
public class DigestUtil {

    /**
     * 对 id 进行 Digest 加密，先是 SHA-1 后 Base64 加密
     * @param s 待加密内容
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String digest(String s) throws NoSuchAlgorithmException {
        return DigestAuthenticationProvider.generateDigest(s);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(digest("zookeeper:admin")); // zookeeper:qW/HnTfCSoQpB5G8LgkwT3IbiFc= 超级用户的账号 zookeeper:admin
    }
}
