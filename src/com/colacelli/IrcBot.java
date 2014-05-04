/* Based on: http://oreilly.com/pub/h/1966 */

package com.colacelli;

public class IrcBot {
    public static void main(String[] args) throws Exception {
        Connection connection = new Connection();
        connection.connect();
    }
}