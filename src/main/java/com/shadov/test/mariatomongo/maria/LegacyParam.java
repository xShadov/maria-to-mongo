package com.shadov.test.mariatomongo.maria;

import javax.persistence.*;

@Entity
@Table(name = "param")
public class LegacyParam {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	private LegacyPack pack;

	private String name;

	private String value;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LegacyPack getPack() {
		return pack;
	}

	public void setPack(LegacyPack pack) {
		this.pack = pack;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
