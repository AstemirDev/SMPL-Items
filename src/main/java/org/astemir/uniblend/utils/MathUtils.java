package org.astemir.uniblend.utils;

import org.astemir.uniblend.misc.TextScanner;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.joml.Vector2d;

public class MathUtils {


    public static Vector2d getRotation(Vector vector){
        return new Vector2d(rotationYaw(vector),rotationPitch(vector));
    }

    public static float rotationYaw(Vector vector){
        float rotation = (float) Math.toDegrees(Math.atan2(-vector.getX(), vector.getZ()));
        return rotation;
    }

    public static float rotationPitch(Vector vector) {
        float rotation = (float) Math.toDegrees(Math.asin(vector.getY()));
        return -rotation;
    }

    public static Vector direction(Location a,Location b){
        return b.clone().subtract(a.clone()).toVector().normalize();
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static Vector lerp(Vector a, Vector b, float t) {
        float x = lerp((float) a.getX(), (float) b.getX(), t);
        float y = lerp((float) a.getY(), (float) b.getY(), t);
        float z = lerp((float) a.getZ(), (float) b.getZ(), t);
        return new Vector(x, y, z);
    }

    public static boolean logicCompare(String expression) {
        TextScanner tokenizer = new TextScanner(expression);
        double value1 = tokenizer.parseNumber().doubleValue();
        tokenizer.skipAllWhitespaces();
        String operator = tokenizer.readUntil((c)->Character.isDigit(c));
        tokenizer.skipAllWhitespaces();
        double value2 = tokenizer.parseNumber().doubleValue();
        switch (operator) {
            case "<":
                return value1 < value2;
            case ">":
                return value1 > value2;
            case "<=":
                return value1 <= value2;
            case ">=":
                return value1 >= value2;
            case "==":
                return value1 == value2;
            case "!=":
                return value1 != value2;
            default:
                throw new RuntimeException("Invalid operator: " + operator);
        }
    }

    public static double mathEval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {

                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) return 0;
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) return 0;
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) return 0;
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    return x;
                } else {
                    return 0;
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

}
