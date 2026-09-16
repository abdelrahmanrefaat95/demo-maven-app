package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AppPropertiesTest {
    @Test void environmentIsStored() { AppProperties p = new AppProperties(); p.setEnvironment("local"); assertEquals("local", p.getEnvironment()); }
    @Test void colorCanBeChanged() { AppProperties p = new AppProperties(); p.setColor("orange"); assertEquals("orange", p.getColor()); }
    @Test void environmentCanBeChanged() { AppProperties p = new AppProperties(); p.setEnvironment("demo"); assertEquals("demo", p.getEnvironment()); }
}
