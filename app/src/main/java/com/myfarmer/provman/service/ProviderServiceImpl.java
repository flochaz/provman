package com.myfarmer.provman.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myfarmer.provman.dao.ProviderDao;
import com.myfarmer.provman.model.Provider;

@Service("providerService")
@Transactional
public class ProviderServiceImpl implements ProviderService {

	@Autowired
	private ProviderDao dao;
	
	public Provider findById(int id) {
		return dao.findById(id);
	}

	public void saveProvider(Provider provider) {
		dao.saveProvider(provider);
	}
	
	public void updateProvider(Provider provider) {
		Provider entity = dao.findById(provider.getId());
		if(entity!=null){
			entity.setName(provider.getName());
			entity.setEnteringDate(provider.getEnteringDate());
			entity.setNationality(provider.getNationality());
			entity.setCode(provider.getCode());
			//dao.saveOrUpdate(provider);
		}
	}

	public void deleteProviderByCode(String ssn) {
		dao.deleteProviderByCode(ssn);
	}
	
	public List<Provider> findAllProviders() {
		return dao.findAllProviders();
	}

	public Provider findProviderByCode(String ssn) {
		return dao.findProviderByCode(ssn);
	}

	public boolean isProviderCodeUnique(Integer id, String ssn) {
		Provider provider = findProviderByCode(ssn);
		return ( provider == null || ((id != null) && (provider.getId() == id)));
	}
	
}
