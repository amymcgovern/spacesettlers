import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import spacesettlers.actions.ActionTests;
import spacesettlers.simulator.SimulatorTests;
import spacesettlers.utilities.UtilitiesTest;
import spacesettlers.ladder.TestLadder;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  ActionTests.class,
  SimulatorTests.class,
  TestLadder.class,
  UtilitiesTest.class,
})

public class RunAllTests {
}


