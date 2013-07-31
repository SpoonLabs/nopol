package fr.inria.lille.jefix.synth.smt.constraint;

import static fr.inria.lille.jefix.synth.smt.model.Components.DISTINCT;
import static fr.inria.lille.jefix.synth.smt.model.Components.EQUALS;
import static fr.inria.lille.jefix.synth.smt.model.Components.LESS_OR_EQUAL_THAN;
import static fr.inria.lille.jefix.synth.smt.model.Components.LESS_THAN;
import static fr.inria.lille.jefix.synth.smt.model.Type.BOOLEAN;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.smtlib.IParser.ParserException;
import org.smtlib.IResponse;
import org.smtlib.ISource;
import org.smtlib.SMT;
import org.smtlib.sexpr.Parser;

import fr.inria.lille.jefix.synth.InputOutputValues;
import fr.inria.lille.jefix.synth.RepairCandidate;
import fr.inria.lille.jefix.synth.smt.model.InputModel;
import fr.inria.lille.jefix.synth.smt.model.ValuesModel;

public class RepairCandidateBuilderTest {


	/**
	 * @return
	 */
	private InputModel createModel() {
		InputOutputValues iov = new InputOutputValues().addInputValue("up_sep", 11).addInputValue("inhibit", 1)
				.addInputValue("down_sep", 110);
		ValuesModel values = new ValuesModel(iov, Arrays.<Object> asList(-1, 0, 1, true, false));
		return new InputModel(asList(BOOLEAN), asList(LESS_THAN, LESS_OR_EQUAL_THAN, EQUALS, DISTINCT),
				BOOLEAN, values);
	}

	/**
	 * @return
	 * @throws ParserException
	 */
	private IResponse createResponse() throws ParserException {
		SMT.Configuration smtConfig = new SMT().smtConfig;
		ISource source = smtConfig.smtFactory
				.createSource(
						"( ( LO 9 ) ( LO_0 10 ) ( L_I0_0 0 ) ( L_I0_1 1 ) ( LO_1 8 ) ( L_I1_0 4 ) ( L_I1_1 1 ) ( LO_2 11 ) ( L_I2_0 1 ) ( L_I2_1 1 ) ( LO_3 9 ) ( L_I3_0 4 ) ( L_I3_1 0 ) )",
						null);
		Parser parser = (Parser) smtConfig.smtFactory.createParser(smtConfig, source);
		return parser.parseSexpr();
	}

	@Test
	public final void test() throws ParserException {
		// GIVEN
		InputModel model = this.createModel();
		IResponse response = this.createResponse();

		// WHEN
		RepairCandidate candidate = new RepairCandidateBuilder(model, response).build();

		// THEN
		assertEquals("0!=up_sep", candidate.toString());
	}
}
