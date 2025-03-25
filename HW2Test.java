import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

/**
 * Tests for the BitSet and Questions classes.
 *
 * <p>This class contains automated tests using JUnit to verify the behavior of the
 * BitSet and Questions classes, including bit indexing methods and question management using the bitset.
 */
class HW2Test {
	/**
	 * Test 1:
	 * 
	 * <p>Verifies that pushing bits sets them correctly
	 * and that PopCount returns the total set bits.
	 */
	@Test
	public void testBitSetPushAndPopCount() {
		BitSet bs = new BitSet();
		bs.Push(0);    /// sets bit 0
		bs.Push(1);    /// sets bit 1
		bs.Push(32);   /// sets bit 32 (in the second slot)
		assertEquals(3, bs.PopCount());
	}
	
	/**
	 * Test 2:
	 * <p>Check that ClearBit and ToggleBit behave as expected.
	 */
	@Test
	public void testBitSetClearAndToggle() {
		var bs = new BitSet();
		bs.Push(5);      assertTrue(bs.TestBit(5));
		bs.ClearBit(5);  assertFalse(bs.TestBit(5));
		bs.ToggleBit(5); assertTrue(bs.TestBit(5));
		bs.ToggleBit(5); assertFalse(bs.TestBit(5));
	}
	
	/**
	 * Test 3:
	 * <p>Test the static utility methods for converting a bit index to slot and bit positions.
	 */
	@Test
	public void testBitIdxToSlotAndBit() {
		/// For bit index 31, it should be in slot 0 and bit position 31.
		assertEquals(0, BitSet.BitIdxToSlot(31));
		assertEquals(31, BitSet.BitIdxToBit(31));
		
		/// For bit index 32, it moves to slot 1 and bit position 0.
		assertEquals(1, BitSet.BitIdxToSlot(32));
		assertEquals(0, BitSet.BitIdxToBit(32));
	}
	
	/**
	 * Test 4:
	 * <p>Add a question and verify that its title, body, tags, and initial answers are set correctly.
	 */
	@Test
	public void testAddQuestion() {
		var questions = new Questions();
		final var question_idx = questions.Add("Test Question", "This is a test body", new String[]{"Java", "UnitTest"});
		assertTrue(question_idx >= 0);
		/// Remember that titles and bodies are converted to lowercase.
		assertEquals("test question", questions.GetTitle(question_idx));
		assertEquals("this is a test body", questions.GetBody(question_idx));
		
		HashSet<String> tags = questions.GetTags(question_idx);
		assertTrue(tags.contains("java"));
		assertTrue(tags.contains("unittest"));
		
		Answers answers = questions.GetAnswers(question_idx);
		assertEquals(0, answers.NumberOf());
	}
	
	/**
	 * Test 5:
	 * <p>Add an answer to a question and mark it as resolved.
	 */
	@Test
	public void testAddAnswerAndMarkResolved() {
		var questions = new Questions();
		final var question_idx = questions.Add("Another Question", "Body of question", new String[]{"tag1"});
		final var answer_idx = questions.AddAnswer(question_idx, "This is an answer");
		assertTrue(answer_idx > 0);
		
		var answers = questions.GetAnswers(question_idx);
		assertEquals(1, answers.NumberOf());
		assertEquals("This is an answer", answers.GetPost(0));
		
		/// Initially a question should be unresolved.
		assertEquals(0, questions.IsResolved(question_idx));
		
		/// Mark an answer as the resolving answer.
		final var resolved = questions.MarkResolved(question_idx, 0);
		assertTrue(resolved);
		assertEquals(1, questions.IsResolved(question_idx));
		assertEquals(0, questions.GetResolver(question_idx));
		
		/// Try to add an empty answer which should be rejected (return -1).
		var badAnswer = questions.AddAnswer(question_idx, "");
		assertEquals(-1, badAnswer);
	}
}
