package com.shadov.test.mariatomongo.mongo;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Set;

@Document
public class ShinyCustomer {
	@Id
	private String id;

	@Indexed
	private String name;

	@Indexed
	private int age;

	@Indexed
	private String city;

	@Indexed
	private LocalDateTime birthDate;

	private Set<ShinyPack> packs;

	public Set<ShinyPack> getPacks() {
		return packs;
	}

	public void setPacks(Set<ShinyPack> packs) {
		this.packs = packs;
	}

	public LocalDateTime getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDateTime birthDate) {
		this.birthDate = birthDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
