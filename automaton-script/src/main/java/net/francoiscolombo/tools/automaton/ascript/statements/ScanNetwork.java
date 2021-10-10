package net.francoiscolombo.tools.automaton.ascript.statements;

import net.francoiscolombo.tools.automaton.ascript.interpreter.Value;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ScanNetwork extends AbstractStatement {

    public ScanNetwork(PrintStream printStream, PrintStream errorStream) {
        super(printStream, errorStream);
    }

    @Override
    public Value visitStatement(Value... values) {
        int exitCode = 1;
        if(check(0, values)) {
            int[] bounds = ScanNetwork.rangeFromCidr(values[0].internalString());
            for (int i = bounds[0]; i <= bounds[1]; i++) {
                String address = InetRange.intToIp(i);
                try {
                    InetAddress ip = InetAddress.getByName(address);
                    if (ip.isReachable(100)) { // Try for one tenth of a second
                        printStream.printf("Address %s is reachable\n", ip);
                    }
                } catch (UnknownHostException e) {
                    errorStream.printf("UnknownHostException: %s", e.getMessage());
                } catch (IOException e) {
                    errorStream.printf("IOException: %s", e.getMessage());
                }
            }
        }
        return new Value(exitCode);
    }


    public static int[] rangeFromCidr(String cidrIp) {
        int maskStub = 1 << 31;
        String[] atoms = cidrIp.split("/");
        int mask = Integer.parseInt(atoms[1]);
        System.out.println(mask);

        int[] result = new int[2];
        result[0] = InetRange.ipToInt(atoms[0]) & (maskStub >> (mask - 1)); // lower bound
        result[1] = InetRange.ipToInt(atoms[0]); // upper bound
        System.out.println(InetRange.intToIp(result[0]));
        System.out.println(InetRange.intToIp(result[1]));

        return result;
    }

    static class InetRange {

        public static int ipToInt(String ipAddress) {
            try {
                byte[] bytes = InetAddress.getByName(ipAddress).getAddress();
                int octet1 = (bytes[0] & 0xFF) << 24;
                int octet2 = (bytes[1] & 0xFF) << 16;
                int octet3 = (bytes[2] & 0xFF) << 8;
                int octet4 = bytes[3] & 0xFF;
                return octet1 | octet2 | octet3 | octet4;
            } catch (Exception e) {
                return 0;
            }
        }

        public static String intToIp(int ipAddress) {
            int octet1 = (ipAddress & 0xFF000000) >>> 24;
            int octet2 = (ipAddress & 0xFF0000) >>> 16;
            int octet3 = (ipAddress & 0xFF00) >>> 8;
            int octet4 = ipAddress & 0xFF;
            return String.valueOf(octet1) + '.' + octet2 + '.' + octet3 + '.' + octet4;
        }

    }

}
