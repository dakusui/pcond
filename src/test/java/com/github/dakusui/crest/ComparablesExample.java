package com.github.dakusui.crest;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class ComparablesExample {
  public abstract static class Base {
    @Test
    public abstract void givenValueCreatedByIdentity$whenMatches$thenPass();

    @Test
    public abstract void givenValueCreatedByIdentity$whenNotMatches$thenFail();

    @Test
    public abstract void givenValueCreatedByFunction$whenNotMatches$thenFail();

    @Test
    public abstract void givenValueCreatedByFunction$whenMatches$thenPass();

    @Test
    public abstract void givenValueCreatedByDynamicCall$whenNotMatches$thenFail();

    @Test
    public abstract void givenValueCreatedByDynamicCall$whenMatches$thenPass();
  }

  public static class DoubleExample extends Base {
    @Override
    public void givenValueCreatedByIdentity$whenMatches$thenPass() {
      Crest.assertThat(
          100.0,
          Crest.asDouble().ge(50.0).matcher()
      );
    }

    @Override
    public void givenValueCreatedByIdentity$whenNotMatches$thenFail() {
      Crest.assertThat(
          100.0,
          Crest.asDouble().lt(50.0).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asDouble(Integer::doubleValue).ge(50.0).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asDouble(Integer::doubleValue).lt(50.0).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asDouble("doubleValue").ge(50.0).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asDouble("doubleValue").lt(50.0).matcher()
      );
    }
  }

  public static class FloatExample extends Base {
    @Override
    public void givenValueCreatedByIdentity$whenMatches$thenPass() {
      Crest.assertThat(
          100.0f,
          Crest.asFloat().ge(50.0f).matcher()
      );
    }

    @Override
    public void givenValueCreatedByIdentity$whenNotMatches$thenFail() {
      Crest.assertThat(
          100.0f,
          Crest.asFloat().lt(50.0f).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asFloat(Integer::floatValue).lt(50.0f).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asFloat(Integer::floatValue).ge(50.0f).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asFloat("floatValue").lt(50.0f).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asFloat("floatValue").ge(50.0f).matcher()
      );
    }
  }

  public static class LongExample extends Base {
    @Override
    public void givenValueCreatedByIdentity$whenMatches$thenPass() {
      Crest.assertThat(
          100L,
          Crest.asLong().ge(50L).matcher()
      );
    }

    @Override
    public void givenValueCreatedByIdentity$whenNotMatches$thenFail() {
      Crest.assertThat(
          100L,
          Crest.asLong().lt(50L).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asLong(Integer::longValue).ge(50L).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asLong(Integer::longValue).lt(50L).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenMatches$thenPass() {
      Crest.assertThat(
          100L,
          Crest.asLong("longValue").ge(50L).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asLong("longValue").lt(50L).matcher()
      );
    }
  }

  public static class IntegerExample extends Base {
    @Override
    public void givenValueCreatedByIdentity$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asInteger().ge(50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByIdentity$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asInteger().lt(50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asInteger(Integer::intValue).ge(50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asInteger(Integer::intValue).lt(50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenMatches$thenPass() {
      Crest.assertThat(
          100L,
          Crest.asInteger("intValue").ge(50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asInteger("intValue").lt(50).matcher()
      );
    }
  }

  public static class ShortExample extends Base {
    @Override
    public void givenValueCreatedByIdentity$whenMatches$thenPass() {
      Crest.assertThat(
          (short) 100,
          Crest.asShort().ge((short) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByIdentity$whenNotMatches$thenFail() {
      Crest.assertThat(
          (short) 100,
          Crest.asShort().lt((short) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asShort(Integer::shortValue).ge((short) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asShort(Integer::shortValue).lt((short) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asShort("shortValue").ge((short) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asShort("shortValue").lt((short) 50).matcher()
      );
    }
  }

  public static class CharExample extends Base {
    @Override
    public void givenValueCreatedByIdentity$whenMatches$thenPass() {
      Crest.assertThat(
          'a',
          Crest.asChar().lt('z').matcher()
      );
    }

    @Override
    public void givenValueCreatedByIdentity$whenNotMatches$thenFail() {
      Crest.assertThat(
          'a',
          Crest.asChar().ge('z').matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenMatches$thenPass() {
      Crest.assertThat(
          'a',
          Crest.asChar(Character.class::cast).lt('z').matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenNotMatches$thenFail() {
      Character c;
      Crest.assertThat(
          'a',
          Crest.asChar(Character.class::cast).ge('z').matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenMatches$thenPass() {
      Crest.assertThat(
          "hello",
          Crest.asChar("charAt", 0).lt('z').matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenNotMatches$thenFail() {
      Crest.assertThat(
          "hello",
          Crest.asChar("charAt", 0).ge('z').matcher()
      );
    }
  }

  public static class ByteExample extends Base {
    @Override
    public void givenValueCreatedByIdentity$whenMatches$thenPass() {
      Crest.assertThat(
          (byte) 100,
          Crest.asByte().ge((byte) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByIdentity$whenNotMatches$thenFail() {
      Crest.assertThat(
          (byte) 100,
          Crest.asByte().lt((byte) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asByte(Integer::byteValue).ge((byte) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asByte(Integer::byteValue).lt((byte) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenMatches$thenPass() {
      Crest.assertThat(
          100,
          Crest.asByte("byteValue").ge((byte) 50).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenNotMatches$thenFail() {
      Crest.assertThat(
          100,
          Crest.asByte("byteValue").lt((byte) 50).matcher()
      );
    }
  }

  public static class BooleanExample extends Base {
    @Override
    public void givenValueCreatedByIdentity$whenMatches$thenPass() {
      Crest.assertThat(
          true,
          Crest.asBoolean().equalTo(true).matcher()
      );
    }

    @Override
    public void givenValueCreatedByIdentity$whenNotMatches$thenFail() {
      Crest.assertThat(
          true,
          Crest.asBoolean().equalTo(false).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenMatches$thenPass() {
      Crest.assertThat(
          "hello",
          Crest.asBoolean(String::isEmpty).equalTo(false).matcher()
      );
    }

    @Override
    public void givenValueCreatedByFunction$whenNotMatches$thenFail() {
      Crest.assertThat(
          "hello",
          Crest.asBoolean(String::isEmpty).equalTo(true).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenMatches$thenPass() {
      Crest.assertThat(
          "hello",
          Crest.asBoolean("isEmpty").equalTo(false).matcher()
      );
    }

    @Override
    public void givenValueCreatedByDynamicCall$whenNotMatches$thenFail() {
      Crest.assertThat(
          "hello",
          Crest.asBoolean("isEmptry").equalTo(true).matcher()
      );
    }
  }
}
