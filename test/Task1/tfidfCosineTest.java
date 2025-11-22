package Task1;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class tfidfCosineTest {

    private Tfidf tfidf;

    @BeforeEach
    void setUp() {
        // Start with empty corpus
        tfidf = new Tfidf(new ArrayList<>());
    }

    // ------------------------
    // C0 – Statement Coverage
    // ------------------------
    @Test
    void testC0_TC1() {
        HashTable d1 = new HashTable();
        d1.add("apple"); d1.add("banana");
        HashTable d2 = new HashTable();
        d2.add("apple"); d2.add("cherry");

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertTrue(cos > 0 && cos <= 1.0);
    }

    // ------------------------
    // C1 – Branch Coverage
    // ------------------------
    @Test
    void testC1_TC1() {
        HashTable d1 = new HashTable();
        d1.add("apple"); d1.add("cherry"); d1.add("cherry"); d1.add("cherry"); d1.add("cherry");
        HashTable d2 = new HashTable();
        d2.add("apple"); d2.add("cherry");

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertTrue(cos > 0 && cos <= 1.0);
    }

    @Test
    void testC1_TC2() {
        HashTable d1 = new HashTable();
        d1.add("apple"); d1.add("cherry");
        HashTable d2 = new HashTable();
        d2.add("banana"); d2.add("orange");

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertEquals(0.0, cos);
    }

    // ------------------------
    // C3b – Minimal Multiple Condition Coverage
    // ------------------------
    @Test
    void testC3_TC3() {
        HashTable d1 = new HashTable();
        d1.add("apple"); d1.add("banana");
        HashTable d2 = new HashTable();
        d2.add("apple"); d2.add("cherry");

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertTrue(cos > 0 && cos <= 1.0);
    }

    @Test
    void testC3_TC4() {
        HashTable d1 = new HashTable();
        d1.add("apple");
        HashTable d2 = new HashTable(); // empty doc

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertEquals(0.0, cos); // magnitude-zero test
    }

    @Test
    void testC3_TC5() {
        HashTable d1 = new HashTable(); // empty
        HashTable d2 = new HashTable();
        d2.add("apple"); d2.add("banana");

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertEquals(0.0, cos); // empty doc triggers mag=0
    }

    @Test
    void testC3_TC6() {
        HashTable d1 = new HashTable();
        d1.add("apple"); d1.add("banana");
        HashTable d2 = new HashTable(); // empty

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertEquals(0.0, cos); // empty doc triggers mag=0
    }

    @Test
    void testC3_TC7() {
        HashTable d1 = new HashTable();
        d1.add("apple");
        assertThrows(NullPointerException.class, () -> tfidf.cosineSimilarity(d1, null));
    }

    @Test
    void testC3_TC8() {
        HashTable d2 = new HashTable();
        d2.add("apple");
        assertThrows(NullPointerException.class, () -> tfidf.cosineSimilarity(null, d2));
    }

    @Test
    void testC3_TC9() {
        HashTable d1 = new HashTable(); // empty
        HashTable d2 = new HashTable(); // empty

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertEquals(0.0, cos); // both empty → mag=0
    }

    @Test
    void testC3_TC10() {
        assertThrows(NullPointerException.class, () -> tfidf.cosineSimilarity(null, null));
    }

    // ------------------------
    // Loop Tests
    // ------------------------
    @Test
    void testLoop_TC1() {
        HashTable d1 = new HashTable(); // empty
        HashTable d2 = new HashTable();
        d2.add("apple");

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertEquals(0.0, cos); // zero iterations
    }

    @Test
    void testLoop_TC2() {
        HashTable d1 = new HashTable();
        d1.add("apple");
        HashTable d2 = new HashTable();
        d2.add("apple");

        tfidf = new Tfidf(new ArrayList<>(List.of(d1, d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertEquals(1.0, cos); // both docs identical → cosine=1
    }

    @Test
    void testLoop_TC3() {
        HashTable d1 = new HashTable();
        d1.add("apple"); d1.add("banana"); d1.add("banana");
        HashTable d2 = new HashTable();
        d2.add("apple"); d2.add("banana"); d2.add("banana"); d2.add("banana");

        tfidf = new Tfidf(new ArrayList<>(List.of(d1,d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertTrue(cos > 0 && cos <= 1.0);
    }

    @Test
    void testLoop_TC4() {
        HashTable d1 = new HashTable(); // empty
        HashTable d2 = new HashTable(); // empty

        tfidf = new Tfidf(new ArrayList<>(List.of(d1,d2)));
        double cos = tfidf.cosineSimilarity(d1, d2);
        assertEquals(0.0, cos); // zero-vector
    }
}
