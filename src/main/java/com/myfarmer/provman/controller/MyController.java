package com.myfarmer.provman.controller;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.myfarmer.provman.model.Provider;
import com.myfarmer.provman.service.ProviderService;

@Controller
@RequestMapping("/")
public class MyController {

	@Autowired
	ProviderService service;
	
	@Autowired
	MessageSource messageSource;

	/*
	 * List all existing Providers.
	 */
	@RequestMapping(value = { "/", "/list" }, method = RequestMethod.GET)
	public String listProviders(ModelMap model) {

		List<Provider> providers = service.findAllProviders();
		model.addAttribute("providers", providers);
		return "allproviders";
	}

	/*
	 * Add a new Provider.
	 */
	@RequestMapping(value = { "/new" }, method = RequestMethod.GET)
	public String newProvider(ModelMap model) {
		Provider provider = new Provider();
		model.addAttribute("provider", provider);
		model.addAttribute("edit", false);
		return "registration";
	}

	/*
	 * Handling POST request for validating the user input and saving Provider in database.
	 */
	@RequestMapping(value = { "/new" }, method = RequestMethod.POST)
	public String saveProvider(@Valid Provider provider, BindingResult result,
			ModelMap model) {

		if (result.hasErrors()) {
			return "registration";
		}
		
		if(!service.isProviderCodeUnique(provider.getId(), provider.getCode())){
			FieldError codeError =new FieldError("Provider","code",messageSource.getMessage("non.unique.code", new String[]{provider.getCode()}, Locale.getDefault()));
		    result.addError(codeError);
			return "registration";
		}
		
		service.saveProvider(provider);

		model.addAttribute("success", "Provider " + provider.getName() + " registered successfully.");
		return "success";
	}


	/*
	 * Provide the existing Provider for updating.
	 */
	@RequestMapping(value = { "/edit-{code}-provider" }, method = RequestMethod.GET)
	public String editProvider(@PathVariable String code, ModelMap model) {
		Provider provider = service.findProviderByCode(code);
		model.addAttribute("provider", provider);
		model.addAttribute("edit", true);
		return "registration";
	}
	
	/*
	 * Handling POST request for validating the user input and updating Provider in database.
	 */
	@RequestMapping(value = { "/edit-{code}-provider" }, method = RequestMethod.POST)
	public String updateProvider(@Valid Provider provider, BindingResult result,
			ModelMap model, @PathVariable String code) {

		if (result.hasErrors()) {
			return "registration";
		}

		if(!service.isProviderCodeUnique(provider.getId(), provider.getCode())){
			FieldError codeError =new FieldError("Provider","code",messageSource.getMessage("non.unique.code", new String[]{provider.getCode()}, Locale.getDefault()));
		    result.addError(codeError);
			return "registration";
		}

		service.updateProvider(provider);

		model.addAttribute("success", "Provider " + provider.getName()	+ " updated successfully");
		return "success";
	}

	
	/*
	 * Delete an Provider by it's CODE value.
	 */
	@RequestMapping(value = { "/delete-{code}-provider" }, method = RequestMethod.GET)
	public String deleteProvider(@PathVariable String code) {
		service.deleteProviderByCode(code);
		return "redirect:/list";
	}

}
