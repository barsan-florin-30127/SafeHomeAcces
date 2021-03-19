package isp.lab7.safehome;

public class TenantAlreadyExistsException extends Exception {
    public TenantAlreadyExistsException() {
        System.out.println("Tenant already exist!");
    }
}
