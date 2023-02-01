package common;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Address implements Serializable {
    private String city;
    private String street;
    private String housenr;
    private String zipcode;

    protected Address(){}

    public Address(String city, String street, String housenr, String zipcode) {
        this.city = city;
        this.street = street;
        this.housenr = housenr;
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHousenr() {
        return housenr;
    }

    public String getZipcode() {
        return zipcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return city.equals(address.city) && street.equals(address.street) && housenr.equals(address.housenr) && zipcode.equals(address.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, housenr, zipcode);
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHousenr(String housenr) {
        this.housenr = housenr;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}