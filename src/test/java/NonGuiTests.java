import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rmblworx.tools.timey.gui.TimeyGuiTest;

@RunWith(Categories.class)
@Categories.ExcludeCategory(TimeyGuiTest.class)
@Suite.SuiteClasses(AllTests.class)
public class NonGuiTests {
}
