package org.astemir.uniblend.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextScanner {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private String text;
    private int index;

    public TextScanner(String text) {
        this.text = text;
        this.index = 0;
    }

    public Boolean parseBoolean() {
        StringBuilder boolStringBuilder = new StringBuilder();
        while (hasNext() && Character.isLetter(currentChar())) {
            boolStringBuilder.append(currentChar());
            nextIndex();
        }
        String boolString = boolStringBuilder.toString();
        if (boolString.equals("true")) {
            return true;
        } else if (boolString.equals("false")) {
            return false;
        } else {
            throw new RuntimeException("Expected 'true' or 'false' but found " + boolString);
        }
    }

    public Object parseNull() {
        String nullString = strOfLength(4);
        if (nullString.equals("null")) {
            nextIndex(4);
            return null;
        } else {
            throw new RuntimeException("Expected null but found " + nullString);
        }
    }

    public Number parseNumber() {
        StringBuilder sb = new StringBuilder();
        char ch = currentChar();
        while (index < text.length() && (Character.isDigit(ch) || ch == '-' || ch == '.')) {
            sb.append(text.charAt(index));
            nextIndex();
            if (index < text.length()) {
                ch = currentChar();
            }
        }
        String numberString = sb.toString();
        if (numberString.matches("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?")) {
            if (numberString.contains(".")) {
                return Double.parseDouble(numberString);
            } else {
                return Integer.parseInt(numberString);
            }
        } else {
            throw new RuntimeException("Invalid JSON number: " + numberString);
        }
    }

    public String parseString() {
        StringBuilder sb = new StringBuilder();
        consume('\"');
        while (!isAt('\"')) {
            char ch = currentChar();
            if (ch == '\\') {
                nextIndex();
                char escapeChar = text.charAt(index);
                if (escapeChar == 'u') {
                    // Handle Unicode escape sequence
                    int unicode = 0;
                    for (int i = 0; i < 4; i++) {
                        nextIndex();
                        char hexChar = currentChar();
                        int hexValue = Character.digit(hexChar, 16);
                        if (hexValue == -1) {
                            throw new IllegalArgumentException("Invalid Unicode escape sequence: \\u" + text.substring(index, index + 4));
                        }
                        unicode = (unicode << 4) + hexValue;
                    }
                    ch = (char) unicode;
                } else {
                    // Handle other escape sequences
                    switch (escapeChar) {
                        case '\"':
                        case '\\':
                        case '/':
                            ch = escapeChar;
                            break;
                        case 'n':
                            ch = '\n';
                            break;
                        case 'r':
                            ch = '\r';
                            break;
                        case 't':
                            ch = '\t';
                            break;
                        case 'b':
                            ch = '\b';
                            break;
                        case 'f':
                            ch = '\f';
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid escape sequence: \\" + escapeChar);
                    }
                }
            }
            sb.append(ch);
            nextIndex();
        }
        consume('\"');
        return sb.toString();
    }



    public boolean consumeIf(char... chs){
        for (char ch : chs) {
            if (isAt(ch)) {
                consume(ch);
                return true;
            }
        }
        return false;
    }

    public char peek(int offset){
        int checkIndex = getIndex()+offset;
        if (checkIndex >= 0 && checkIndex < text.length()){
            return text.charAt(checkIndex);
        } else {
            throw new RuntimeException("Invalid index: " + checkIndex);
        }
    }

    public void consume(char... chs) {
        for (char ch : chs) {
            if (text.charAt(index) == ch) {
                nextIndex();
            } else {
                throw new RuntimeException("Expected '" + ch + "' but found '" + text.charAt(index) + "'");
            }
        }
    }

    public boolean consumeIf(String substr){
        if (isAt(substr)){
            nextIndex(substr.length());
            return true;
        }
        return false;
    }

    public void consume(String substr){
        if (isAt(substr)){
            nextIndex(substr.length());
        }
    }

    public void consumeEOL(){
        consume(LINE_SEPARATOR);
    }

    public boolean hasNext(){
        return index < text.length();
    }

    public int nextIndex(){
        return nextIndex(1);
    }

    public char nextChar(int i){
        return charAt(nextIndex(i));
    }

    public char nextChar(){
        return nextChar(1);
    }

    public char charAt(int index){
        if (index >= 0 && index < text.length()) {
            return getText().charAt(index);
        }
        throw new IndexOutOfBoundsException("Index out of range: " + index);
    }
    public int nextIndex(int i) {
        if (index + i > text.length()) {
            throw new RuntimeException("No next character");
        }
        index += i;
        return index - 1;
    }

    public char currentChar(){
        if (index >= 0 && index < text.length()) {
            return text.charAt(index);
        }
        throw new IndexOutOfBoundsException("Index out of range: " + index);
    }


    public boolean isAt(char... chs){
        if (index < 0 || index >= text.length()) {
            return false;
        }
        for (char ch : chs) {
            if (text.charAt(index) == ch) {
                return true;
            }
        }
        return false;
    }

    public boolean isAt(String substr) {
        if (index < 0 || index+substr.length() >= text.length()) {
            return false;
        }else{
            return strOfLength(substr.length()).equals(substr);
        }
    }

    public void skipAll(char... chars) {
        while (hasNext() && isAt(chars)) {
            nextIndex();
        }
    }

    public void skipWhitespace(){
        if (isOnWhitespace()){
            nextIndex();
        }
    }

    public void skipAllWhitespaces() {
        while (hasNext() && Character.isWhitespace(text.charAt(index))) {
            nextIndex();
        }
    }


    public String readUntilString(String substr){
        StringBuilder varBuilder = new StringBuilder();
        while (hasNext() && !isAt(substr)) {
            varBuilder.append(currentChar());
            nextIndex();
        }
        return varBuilder.toString();
    }

    public String readUntilChar(char... chars){
        StringBuilder varBuilder = new StringBuilder();
        while (hasNext() && !isAt(chars)) {
            varBuilder.append(currentChar());
            nextIndex();
        }
        return varBuilder.toString();
    }

    public String readUntilEOL(){
        return readUntilString(LINE_SEPARATOR);
    }

    public String readUntil(Predicate<Character> predicate){
        StringBuilder varBuilder = new StringBuilder();
        while (hasNext() && !predicate.test(currentChar())) {
            varBuilder.append(currentChar());
            nextIndex();
        }
        return varBuilder.toString();
    }
    public String readUntilSpace(){
        StringBuilder varBuilder = new StringBuilder();
        while (hasNext() && !isOnWhitespace()) {
            varBuilder.append(currentChar());
            nextIndex();
        }
        return varBuilder.toString();
    }

    public String readInside(char begin,char end) {
        Stack<Character> braceStack = new Stack<>();
        nextIndex(index);
        consume(begin);
        StringBuilder contentBuilder = new StringBuilder();
        while (hasNext()) {
            char currentChar = currentChar();
            if (currentChar == begin) {
                braceStack.push(currentChar);
            } else if (currentChar == end) {
                if (braceStack.isEmpty()) {
                    break;
                }else{
                    braceStack.pop();
                }
            }
            contentBuilder.append(currentChar);
            nextIndex();
        }
        return contentBuilder.toString();
    }

    public String readAllSkip(char... skip){
        StringBuilder varBuilder = new StringBuilder();
        while (hasNext()) {
            if (!isAt(skip)) {
                varBuilder.append(currentChar());
            }
            nextIndex();
        }
        return varBuilder.toString();
    }

    public String readUntilSkipAll(char c,char... skip){
        StringBuilder varBuilder = new StringBuilder();
        while (hasNext() && currentChar() != c) {
            if (!isAt(skip)) {
                varBuilder.append(currentChar());
            }
            nextIndex();
        }
        return varBuilder.toString();
    }

    public TextScanner scan(){
        TextScanner newScanner = new TextScanner(remaining());
        return newScanner;
    }

    public boolean isOnWhitespace(){
        return index >= 0 && index < text.length() && Character.isWhitespace(text.charAt(index));
    }

    public String remaining(){
        return text.substring(index);
    }
    public String strOfLength(int length){
        if (index+length <= text.length()) {
            return text.substring(index, index + length);
        }
        return "";
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return index;
    }

    public void resetIndex(int index) {
        this.index = index;
    }

    public static String[] regex(String string,String regex){
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(string);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(0));
        }
        return list.toArray(new String[list.size()]);
    }
}
