package isp.lab7.safehome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SafeHome {

    public static void main(String[] args) throws Exception {
        List<Tenant> tenantList = new ArrayList<>();
        List<AccessKey> accessKeyList = new ArrayList<>();
        List<AccessLog> accessLogs = new ArrayList<>();
        Door door = new Door();
        Tenant tenant1 = new Tenant("Sally");
        AccessKey accessKey1 = new AccessKey("12345");
        tenantList.add(tenant1);
        accessKeyList.add(accessKey1);
        Map<Tenant, AccessKey> validAccess = new HashMap<>();
        DoorLockController ctrl = new DoorLockController(validAccess, door, tenantList, accessKeyList, accessLogs);
        ctrl.addTenant("1234", "John");
        ctrl.enterPin("1234");
        //ctrl.addTenant("123","John");
        ctrl.addTenant("123456", "Cena");
        ctrl.enterPin("123456");
        ctrl.removeTenant("John");
        //ctrl.enterPin("1234");
        // ctrl.enterPin("0000");
        //ctrl.enterPin("0000");
        //ctrl.enterPin("0000");
        ctrl.enterPin("MasterKeyValue");
        // ctrl.removeTenant("dontExist");
        ctrl.getAccessLogs();
    }
}
