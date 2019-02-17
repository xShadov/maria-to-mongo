package com.shadov.test.mariatomongo.maria;

import com.shadov.test.mariatomongo.mongo.ShinyCustomer;
import com.shadov.test.mariatomongo.mongo.ShinyPack;
import com.shadov.test.mariatomongo.mongo.ShinyParam;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "customers")
/*@NamedEntityGraph(name = "FULL_GRAPH",
		attributeNodes = @NamedAttributeNode(value = "packs")
)*/
@NamedEntityGraph(name = "FULL_GRAPH",
		attributeNodes = @NamedAttributeNode(
				value = "packs",
				subgraph = "param"
		),
		subgraphs = @NamedSubgraph(
				name = "param",
				attributeNodes = @NamedAttributeNode("param")
		)
)
public class LegacyCustomer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String name;

	private Integer age;

	private String city;

	private LocalDateTime birthDate;

	@OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
	private Set<LegacyPack> packs;

	public LocalDateTime getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDateTime birthDate) {
		this.birthDate = birthDate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Set<LegacyPack> getPacks() {
		return packs;
	}

	public void setPacks(Set<LegacyPack> packs) {
		this.packs = packs;
	}

	public ShinyCustomer toShinyCustomer() {
		ShinyCustomer shiny = new ShinyCustomer();
		shiny.setName(this.getName());
		shiny.setAge(this.getAge());
		shiny.setCity(this.getCity());
		shiny.setBirthDate(this.getBirthDate());

		shiny.setPacks(this.getPacks().stream().map(this::toShinyPack).collect(Collectors.toSet()));

		return shiny;
	}

	private ShinyPack toShinyPack(LegacyPack legacyPack) {
		return new ShinyPack(legacyPack.getParam().stream().map(this::toShinyParam).collect(Collectors.toSet()));
	}

	private ShinyParam toShinyParam(LegacyParam legacyParam) {
		return new ShinyParam(legacyParam.getName(), legacyParam.getValue());
	}
}
