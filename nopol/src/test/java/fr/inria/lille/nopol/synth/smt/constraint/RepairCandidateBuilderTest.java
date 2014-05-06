package fr.inria.lille.nopol.synth.smt.constraint;

import static fr.inria.lille.nopol.synth.smt.model.Components.DISTINCT;
import static fr.inria.lille.nopol.synth.smt.model.Components.EQUALS;
import static fr.inria.lille.nopol.synth.smt.model.Components.LESS_OR_EQUAL_THAN;
import static fr.inria.lille.nopol.synth.smt.model.Components.LESS_THAN;
import static fr.inria.lille.nopol.synth.smt.model.Type.BOOLEAN;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.junit.Test;
import org.smtlib.IParser.ParserException;
import org.smtlib.IResponse;
import org.smtlib.ISource;
import org.smtlib.SMT;
import org.smtlib.sexpr.Parser;

import com.google.common.collect.ImmutableMap;

import fr.inria.lille.nopol.synth.InputOutputValues;
import fr.inria.lille.nopol.synth.RepairCandidate;
import fr.inria.lille.nopol.synth.smt.model.Component;
import fr.inria.lille.nopol.synth.smt.model.InputModel;
import fr.inria.lille.nopol.synth.smt.model.Level;
import fr.inria.lille.nopol.synth.smt.model.ValuesModel;

public class RepairCandidateBuilderTest {

	/**
	 * @return
	 */
	private InputModel createModel() {
		InputModel simpleModel = this.createSimpleModel();
		simpleModel.getComponents().addAll(asList(LESS_THAN, LESS_OR_EQUAL_THAN, EQUALS, DISTINCT));
		return simpleModel;
	}

	/**
	 * @return
	 * @throws ParserException
	 */
	private IResponse createResponse(final String response) throws ParserException {
		SMT.Configuration smtConfig = new SMT().smtConfig;
		ISource source = smtConfig.smtFactory.createSource(response, null);
		Parser parser = (Parser) smtConfig.smtFactory.createParser(smtConfig, source);
		return parser.parseSexpr();
	}

	/**
	 * @return
	 */
	private InputModel createSimpleModel() {
		Iterable<Map.Entry<String, Object>> inputValues = ImmutableMap.<String, Object> of("up_sep", 11, "inhibit", 1,
				"down_sep", 110).entrySet();
		InputOutputValues iov = new InputOutputValues().addValues(inputValues, true);
		ValuesModel values = new ValuesModel(iov, Arrays.<Object> asList(-1, 0, 1, true, false));
		return new InputModel(asList(BOOLEAN), new ArrayList<Component>(), BOOLEAN, values, Level.CONSTANTS);
	}

	@Test
	public final void just_a_boolean_constant() throws ParserException {
		// GIVEN
		InputModel model = this.createSimpleModel();
		IResponse response = this.createResponse("( ( LO 6 ) )");

		// WHEN
		RepairCandidate candidate = new RepairCandidateBuilder(model, response).build();

		// THEN
		assertEquals("true", candidate.toString());
	}

	@Test
	public final void just_a_variable() throws ParserException {
		// GIVEN
		InputModel model = this.createSimpleModel();
		IResponse response = this.createResponse("( ( LO 1 ) )");

		// WHEN
		RepairCandidate candidate = new RepairCandidateBuilder(model, response).build();

		// THEN
		assertEquals("inhibit", candidate.toString());
	}

	@Test
	public final void just_an_integer_constant() throws ParserException {
		// GIVEN
		InputModel model = this.createSimpleModel();
		IResponse response = this.createResponse("( ( LO 3 ) )");

		// WHEN
		RepairCandidate candidate = new RepairCandidateBuilder(model, response).build();

		// THEN
		assertEquals("-1", candidate.toString());
	}

	@Test
	public final void just_one_component() throws ParserException {
		// GIVEN
		InputModel model = this.createModel();
		IResponse response = this
				.createResponse("( ( LO 9 ) ( LO_0 10 ) ( L_I0_0 0 ) ( L_I0_1 1 ) ( LO_1 8 ) ( L_I1_0 4 ) ( L_I1_1 1 ) ( LO_2 11 ) ( L_I2_0 1 ) ( L_I2_1 1 ) ( LO_3 9 ) ( L_I3_0 4 ) ( L_I3_1 0 ) )");

		// WHEN
		RepairCandidate candidate = new RepairCandidateBuilder(model, response).build();

		// THEN
		assertEquals("(0)!=(up_sep)", candidate.toString());
	}

	/**
	 * XXX FIXME TODO this should fail, it's not taking into account the parameter types of the components
	 * 
	 * @throws ParserException
	 */
	@Test
	public final void more_than_one_component() throws ParserException {
		// GIVEN
		InputModel model = this.createModel();
		IResponse response = this
				.createResponse("( ( LO 11 ) ( LO_0 10 ) ( L_I0_0 8 ) ( L_I0_1 1 ) ( LO_1 8 ) ( L_I1_0 4 ) ( L_I1_1 1 ) ( LO_2 11 ) ( L_I2_0 9 ) ( L_I2_1 10 ) ( LO_3 9 ) ( L_I3_0 4 ) ( L_I3_1 0 ) )");

		// WHEN
		RepairCandidate candidate = new RepairCandidateBuilder(model, response).build();

		// THEN
		assertEquals("((0)!=(up_sep))==(((0)<=(inhibit))<(inhibit))", candidate.toString());
	}
}
