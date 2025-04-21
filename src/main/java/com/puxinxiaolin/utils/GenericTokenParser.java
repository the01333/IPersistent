package com.puxinxiaolin.utils;

public class GenericTokenParser {

    /**
     * 开始标记
     */
    private final String openToken;

    /**
     * 结束标记
     */
    private final String closeToken;

    /**
     * 标记处理器
     */
    private final TokenHandler handler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    /**
     * 解析 ${} 和 #{}
     * @param text
     * @return
     */
    public String parse(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        int start = text.indexOf(openToken, 0);
        if (start == -1) {
            return text;
        }

        char[] src = text.toCharArray();
        int offset = 0;
        final StringBuilder sb = new StringBuilder();
        StringBuilder expression = null;
        while (start > -1) {
            // 如果存在转义字符，不作为 openToken 处理
            if (start > 0 && src[start - 1] == '\\') {
                sb.append(src, offset, start - offset - 1).append(openToken);
                offset = start + openToken.length();
            } else {
                // 重置 expression，避免旧值干扰
                if (expression == null) {
                    expression = new StringBuilder();
                } else {
                    expression.setLength(0);
                }

                sb.append(src, offset, start - offset);
                offset = start + openToken.length();
                int end = text.indexOf(closeToken, offset);
                while (end > -1) {
                    // 如果存在转义字符，不作为 openToken 处理
                    if (end > offset && src[end - 1] == '\\') {
                        expression.append(src, offset, end - offset - 1);
                        offset = end + closeToken.length();
                        end = text.indexOf(closeToken, offset);
                    } else {
                        expression.append(src, offset, end - offset);
                        offset = end + closeToken.length();
                        break;
                    }
                }

                // 全部替换完之后
                if (end == -1) {
                    sb.append(src, offset, src.length - offset);
                    offset = src.length;
                } else {
                    sb.append(handler.handleToken(expression.toString()));
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }

        if (offset < src.length) {
            sb.append(src, offset, src.length - offset);
        }
        return sb.toString();
    }
}
