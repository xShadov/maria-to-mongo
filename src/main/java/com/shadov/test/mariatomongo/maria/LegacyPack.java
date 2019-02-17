package com.shadov.test.mariatomongo.maria;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "packs")
public class LegacyPack {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	private LegacyCustomer customer;

	@OneToMany(mappedBy = "pack", fetch = FetchType.EAGER)
	private Set<LegacyParam> param;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LegacyCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(LegacyCustomer customer) {
		this.customer = customer;
	}

	public Set<LegacyParam> getParam() {
		return param;
	}

	public void setParam(Set<LegacyParam> param) {
		this.param = param;
	}
}
