package com.subtitlescorrector.core.domain.ai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"usage","service_tier","system_fingerprint"})
public class ChatResponse {

	String id;
	String object;
	String created;
	String model;
	List<Choice> choices;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public List<Choice> getChoices() {
		return choices;
	}
	public void setChoices(List<Choice> choices) {
		this.choices = choices;
	}
	public String getFirstChoiceMessage() {
		
		if(choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
			return choices.get(0).getMessage().getContent();
		}else {
			return null;
		}
		
	}
	
}
