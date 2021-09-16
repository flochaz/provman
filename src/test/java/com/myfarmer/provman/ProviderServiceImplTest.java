package com.myfarmer.provman;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.myfarmer.provman.dao.ProviderDao;
import com.myfarmer.provman.model.Provider;
import com.myfarmer.provman.service.ProviderService;
import com.myfarmer.provman.service.ProviderServiceImpl;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ProviderServiceImplTest {

    private ProviderService service;

    // Mock the DB component
    @Mock
	private ProviderDao dao;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		service = new ProviderServiceImpl(dao);
	}

	@Test
	void testFindById() {
        Provider provider = new Provider(1,"name",LocalDate.now(),"nationality","code");
        Mockito.doReturn(provider).when(dao).findById(1);

        // Execute the service call
        Provider returnedProvider = service.findById(1);

        // Assert the response
        assertTrue(returnedProvider != null, "Provider was not found");
        assertSame(returnedProvider, provider, "The Provider returned was not the same as the mock");
	}

	@Test
	void testFindAllProviders() {
		List<Provider> allProviders = new ArrayList<Provider>();
		Provider provider = new Provider(1,"name",LocalDate.now(),"nationality","code");
		Provider provider2 = new Provider(2,"name2",LocalDate.now(),"nationality2","code2");
		allProviders.add(provider);
		allProviders.add(provider2);
        Mockito.doReturn(allProviders).when(dao).findAllProviders();

        // Execute the service call
        List<Provider> returnedProviders = service.findAllProviders();

        // Assert the response
        assertTrue(returnedProviders != null && !returnedProviders.isEmpty(), "Providers not found");
        assertTrue(returnedProviders.size() == 2, "The Provider returned was not the same as the mock");
	}
	
	@Test
	void testFindByIdNotExists() {
        Provider provider = new Provider(1,"name",LocalDate.now(),"nationality","code");
        Mockito.doReturn(provider).when(dao).findById(1);

        // Execute the service call
        Provider returnedProvider = service.findById(999);

        // Assert the response
        assertTrue(returnedProvider == null, "Bad Provider returned");
	}
	
	@Test
	void testUpdateProvider() {
		LocalDate now = LocalDate.now();
		Provider provider = new Provider(1,"name-a", now, "nationality-a", "code-a");
        Mockito.doReturn(provider).when(dao).findById(1);

        Provider providerUpdated = new Provider(1, "name-b", now, "nationality-b", "code-b");
        // Execute the service call
        service.updateProvider(providerUpdated);
        
        // check that the original provider now has vars matching the updated provider
        assertTrue(provider.equals(providerUpdated), "The Provider returned was not the same as the mock");
                
	}
}
