package org.eclipse.update.tests.parser;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.net.URL;

import org.eclipse.update.core.*;
import org.eclipse.update.core.model.*;
import org.eclipse.update.core.model.DefaultSiteParser;
import org.eclipse.update.core.model.SiteMapModel;
import org.eclipse.update.internal.core.*;
import org.eclipse.update.internal.core.SiteFileFactory;
import org.eclipse.update.internal.core.UpdateManagerUtils;
import org.eclipse.update.tests.UpdateManagerTestCase;

public class TestSiteParse extends UpdateManagerTestCase {

	/**
	 * Constructor for Test1
	 */
	public TestSiteParse(String arg0) {
		super(arg0);
	}

	public void testParse() throws Exception {

		URL remoteUrl = new URL(SOURCE_FILE_SITE + "xmls/site1/");
		ISite remoteSite = SiteManager.getSite(remoteUrl);

		IFeatureReference[] feature = remoteSite.getFeatureReferences();
		ICategory[] categories = remoteSite.getCategories();

		String path = remoteUrl.getFile();
		String path2 = remoteSite.getInfoURL().getFile();
		assertEquals(path + "info/siteInfo.html", path2);

	}

	public void testNumberOfFeatures() throws Exception {

		URL remoteURL = new URL("http", getHttpHost(), getHttpPort(), bundle.getString("HTTP_PATH_2"));
		ISite remoteSite = SiteManager.getSite(remoteURL);

		IFeatureReference[] feature = remoteSite.getFeatureReferences();
		assertEquals(feature.length, 2);

	}

	public void testParseValid1() throws Exception {

		URL remoteURL = new URL(SOURCE_FILE_SITE + "parsertests/site.xml");
		DefaultSiteParser parser = new DefaultSiteParser(new SiteFileFactory());
		URL resolvedURL = URLEncoder.encode(remoteURL);		
		SiteMapModel remoteSite = parser.parse(resolvedURL.openStream());
		remoteSite.resolve(remoteURL, null);

		FeatureReferenceModel[] feature = remoteSite.getFeatureReferenceModels();
		SiteCategoryModel[] categories = remoteSite.getCategoryModels();
		ArchiveReferenceModel[] archives = remoteSite.getArchiveReferenceModels();

		assertTrue("Wrong number of features", feature.length == 6);
		assertTrue("Wrong number of categories", categories.length == 3);
		assertTrue("Wrong number of archives", archives.length == 0);

		String path = new URL(SOURCE_FILE_SITE + "parsertests/").getFile();
		String path2 = remoteSite.getDescriptionModel().getURL().getFile();
		assertEquals(path + "index.html", path2);

	}

	public void testParseValid2() throws Exception {

		URL remoteURL = new URL(SOURCE_FILE_SITE + "parsertests/reddot.xml");
		DefaultSiteParser parser = new DefaultSiteParser(new SiteFileFactory());
		URL resolvedURL = URLEncoder.encode(remoteURL);		
		SiteMapModel remoteSite = parser.parse(resolvedURL.openStream());
		remoteSite.resolve(remoteURL, null);

		FeatureReferenceModel[] feature = remoteSite.getFeatureReferenceModels();
		SiteCategoryModel[] categories = remoteSite.getCategoryModels();
		ArchiveReferenceModel[] archives = remoteSite.getArchiveReferenceModels();

		assertTrue("Wrong number of features", feature.length == 2);
		assertTrue("Wrong number of categories", categories.length == 1);
		assertTrue("Wrong number of archives", archives.length == 2);

		// FIXME teh parse doesn't return speace before < and after >
		String valideString = "This category contains all of the<currently>available versions of Red Dot feature.";
		assertEquals(valideString,remoteSite.getCategoryModels()[0].getDescriptionModel().getAnnotation());

		String path = new URL(SOURCE_FILE_SITE + "parsertests/").getFile();
		String path2 = remoteSite.getDescriptionModel().getURL().getFile();
		assertEquals(path + "info/siteInfo.html", path2);

	}

}