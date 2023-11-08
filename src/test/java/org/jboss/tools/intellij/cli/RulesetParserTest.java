package org.jboss.tools.intellij.cli;

import junit.framework.TestCase;
import org.jboss.tools.intellij.windup.cli.RulesetParser;
import org.jboss.tools.intellij.windup.model.KantraConfiguration;

import java.util.List;

public class RulesetParserTest extends TestCase {
        public void testParseRuleset() {
            String testFilePath = "src/test/java/org/jboss/tools/intellij/cli/test.yaml";

            List<KantraConfiguration.Ruleset> result = RulesetParser.parseRuleset(testFilePath);
             assertNotNull(result);
            assertFalse(result.isEmpty());
            for (KantraConfiguration.Ruleset ruleset : result) {
                assertTrue(ruleset.getSkipped().isEmpty());
            }
            assertEquals(3,result.size());
        }

    }
