package test.drivojoy.inventory;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.drivojoy.inventory.InventoryAppApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = InventoryAppApplication.class)
@Rollback(value=true)
@ActiveProfiles("test")
public abstract class InventoryAppTests {
	
	protected static final Logger logger = LoggerFactory.getLogger(InventoryAppTests.class);

}
