package io.github.buniaowanfeng.util;

import java.util.Scanner;

/**
 * Created by caofeng on 16-9-7.
 */
public class Encription {
    public static String encode(String origin,int key){
        StringBuffer coded = new StringBuffer();
        char[] chars = origin.toCharArray();
        for (char c : chars){
            if (c > 'a' && c < 'z'){
               coded.append(convert(c,key,'a'));
            }else if(c > 'A' && c < 'Z'){
                coded.append( convert(c,key,'A'));
            }else {
                coded.append(c);
            }
        }

        return coded.toString();
    }

    public static String decode(String origin,int key){
        return encode(origin,-key);
    }
    private static int convert(char c, int key, char start) {
        int startInt = (int)start;
        int offset = ((int)c - startInt + key)%26 ;
        return (char)(startInt + offset);
    }

    public static void  main(String[] args){
        System.out.print("enter a string to encode\n");
        Scanner scanner = new Scanner(System.in);
        String pass = scanner.nextLine();
        System.out.print("\nafter encode\n");
        String a = encode(pass,3);
        System.out.print("\n"+a);
        System.out.print("\nafter decode\n");
        String b = decode(a,3);
        System.out.print("\n"+b);
    }
}
