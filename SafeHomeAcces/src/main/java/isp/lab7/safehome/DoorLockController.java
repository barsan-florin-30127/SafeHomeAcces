package isp.lab7.safehome;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Main class that implements Controller interface and represents the door lock controller.
 */
public class DoorLockController implements ControllerInterface {
    private Map<Tenant, AccessKey> validAccess;
    private Door door;
    private List<Tenant> tenantList;
    private List<AccessKey> accessKeyList;
    private List<AccessLog> accessLogs;
    private String MASTER_KEY;
    private String MASTER_TENANT_NAME;
    /**
     * Public variable that represents number of times the pin has been introduced wrong.
     */
    public int numberOfIncorrectIntroducedPin;

    /**
     * Main constructor.
     *
     * @param validAccess   map of Tenant key (name of the tenant) and AccesKey value (tenant's pin)
     * @param door
     * @param tenantList    arrayList which contains all the tenants which have access to our lock
     * @param accessKeyList arrayList which contains all the pins which give access to tenants to open/close door.
     * @param accessLogs    arrayList which contains all operations made on the doorLock
     */
    public DoorLockController(Map<Tenant, AccessKey> validAccess, Door door, List<Tenant> tenantList, List<AccessKey> accessKeyList, List<AccessLog> accessLogs) {
        this.validAccess = validAccess;
        this.door = door;
        this.tenantList = tenantList;
        this.accessKeyList = accessKeyList;
        this.accessLogs = accessLogs;
        this.MASTER_KEY = ControllerInterface.MASTER_KEY;
        this.MASTER_TENANT_NAME = ControllerInterface.MASTER_TENANT_NAME;
        this.numberOfIncorrectIntroducedPin=0;
    }

    /**
     * Method that make sure that each tenant introduce a right pin to have access to open/close the door.
     *
     * @param pin - pin value.
     * @return door status (open/close).
     * @throws TooManyAttemptsException is thrown when an invalid pin has been entered three or more times.
     * @throws InvalidPinException      is thrown when an invalid pin has been introduced one or two times.
     */
    @Override
    public DoorStatus enterPin(String pin) throws TooManyAttemptsException, InvalidPinException {
        AccessKey accessKeyTemporary = new AccessKey(pin);
        int tenantPinIndex = 0;
        int flag = 0;
        for (int i = 0; i < this.accessKeyList.size(); i++) {
            if (accessKeyList.get(i).equals(accessKeyTemporary)) {
                flag = 1;
                tenantPinIndex = i;
            }
        }//verificam daca master_key-ul a fost introdus
        if (accessKeyTemporary.equals(new AccessKey(MASTER_KEY))) {//tratam cazul in care este introdus master_key-ul
            numberOfIncorrectIntroducedPin = 0;//reinitializam numarul de ture in care poate fi introdus un pin gresit
            if (this.door.getStatus() == DoorStatus.CLOSE) {
                this.door.unlockDoor();
                System.out.println("Door was unlocked");
                this.accessLogs.add(new AccessLog(this.MASTER_TENANT_NAME, LocalDateTime.now(), "Enter Pin", this.door.getStatus(), "Correct Pin"));
            } else {
                this.door.lockDoor();
                System.out.println("Door was locked");
                this.accessLogs.add(new AccessLog(this.MASTER_TENANT_NAME, LocalDateTime.now(), "Enter Pin", this.door.getStatus(), "Correct Pin"));

            }
            return this.door.getStatus();
        } else {
            if (numberOfIncorrectIntroducedPin == 2) {//if ce trateaza cazul in care este introdus un pin gresit de mai multe ori
                this.accessLogs.add(new AccessLog("A tenant ", LocalDateTime.now(), "Enter pin", this.door.getStatus(), "Too many incorrect pins entered"));
                throw new TooManyAttemptsException();
            } else {
                if (flag == 1) {//tratam cazul in care este introdus un pin regasit in lista cu accessKey-uri
                    if (this.door.getStatus() == DoorStatus.CLOSE) {
                        this.door.unlockDoor();
                        System.out.println("Door was unlocked");
                        this.accessLogs.add(new AccessLog(tenantList.get(tenantPinIndex).getName(), LocalDateTime.now(), "Enter Pin", this.door.getStatus(), "Correct Pin"));
                    } else {
                        this.door.lockDoor();
                        System.out.println("Door was locked");
                        this.accessLogs.add(new AccessLog(tenantList.get(tenantPinIndex).getName(), LocalDateTime.now(), "Enter Pin", this.door.getStatus(), "Correct Pin"));

                    }
                    return this.door.getStatus();
                } else {//tratam cazul in care este introdus un pin gresit
                    this.accessLogs.add(new AccessLog("A tenant ", LocalDateTime.now(), "Enter pin", this.door.getStatus(), "Incorrect Pin"));
                    numberOfIncorrectIntroducedPin++;
                    throw new InvalidPinException();
                }
            }
        }
    }

    /**
     * Method that add a new Tenant that can have access to our door.
     *
     * @param pin  - pin to be added in the system
     * @param name - tenant name to be added in the system
     * @throws TenantAlreadyExistsException is thrown where the Tenant that we want to add is already in the system.
     */
    @Override
    public void addTenant(String pin, String name) throws TenantAlreadyExistsException {
        Tenant temporaryTenant = new Tenant(name);
        int flag = 0;
        for (int i = 0; i < this.tenantList.size(); i++) {
            if (tenantList.get(i).equals(temporaryTenant)) {
                flag = 1;
            }
        }
        if (flag == 1) {
            this.accessLogs.add(new AccessLog(temporaryTenant.getName(), LocalDateTime.now(), "Add Tenant", this.door.getStatus(), "Tenant Already Exists"));
            throw new TenantAlreadyExistsException();
        } else {
            tenantList.add(temporaryTenant);
            AccessKey accessKey = new AccessKey(pin);
            accessKeyList.add(accessKey);
            validAccess.put(temporaryTenant, accessKey);
            this.accessLogs.add(new AccessLog(temporaryTenant.getName(), LocalDateTime.now(), "Add Tenant", this.door.getStatus(), "Tenant has been added"));
        }
    }

    /**
     * Method that remore a tenant from the system.
     *
     * @param name - tenant name to be removed
     * @throws TenantNotFoundException is thrown when the Tenant that we want to remove is not found in the system.(It wasn't added)
     */
    @Override
    public void removeTenant(String name) throws TenantNotFoundException {
        Tenant temporaryTenant = new Tenant(name);
        int flag = 0;
        int removedTenantIndex = 0;
        for (int i = 0; i < this.tenantList.size(); i++) {
            if (tenantList.get(i).equals(temporaryTenant)) {
                flag = 1;
                removedTenantIndex = i;
            }
        }
        if (flag == 1) {
            this.accessLogs.add(new AccessLog(temporaryTenant.getName(), LocalDateTime.now(), "Remove Tenant", this.door.getStatus(), "Tenant has been removed"));
            accessKeyList.remove(removedTenantIndex);
            tenantList.remove(removedTenantIndex);
            validAccess.remove(temporaryTenant);
        } else {
            this.accessLogs.add(new AccessLog(temporaryTenant.getName(), LocalDateTime.now(), "Remove Tenant", this.door.getStatus(), "Tenant Not Found"));
            throw new TenantNotFoundException();
        }
    }

    /**
     * Method that shows the accessLogs from the system.
     *
     * @return reference to the accessLogs and print on-screen it's values.
     */
    public List<AccessLog> getAccessLogs() {
        for (int i = 0; i < accessLogs.size(); i++) {
            System.out.println(this.accessLogs.get(i).toString());
        }
        return accessLogs;
    }
}
