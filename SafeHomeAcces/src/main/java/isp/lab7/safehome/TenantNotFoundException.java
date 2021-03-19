package isp.lab7.safehome;

public class TenantNotFoundException extends Exception {
    public TenantNotFoundException() {
        System.out.println("Tenant not found");
    }
}
