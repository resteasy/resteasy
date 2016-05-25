package se.unlogic.standardutils.populators;


public class DummyPopulator implements BeanStringPopulator<Object> {

	public boolean validateFormat(String value) {

		return false;
	}

	public Object getValue(String value) {

		return null;
	}

	public Class<Object> getType() {

		return null;
	}

	public String getPopulatorID() {

		return null;
	}
}
