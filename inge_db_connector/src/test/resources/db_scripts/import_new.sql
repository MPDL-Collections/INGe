--
-- Name: user_login; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--
ALTER TABLE IF EXISTS public.user_login DROP CONSTRAINT user_login_loginname_fkey2;
ALTER TABLE IF EXISTS public.user_login DROP CONSTRAINT user_login_pkey;
DROP TABLE IF EXISTS public.user_login;


--
-- Name: user_login; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
-- Is to be created manually, because not provided by Hibernate.
--

CREATE TABLE user_login (loginname character varying(255) NOT NULL, password character varying(255));


ALTER TABLE user_login OWNER TO postgres;

--
-- Name: user_login_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_login ADD CONSTRAINT user_login_pkey PRIMARY KEY (loginname);
    


--
-- Name: user_login_loginname_fkey2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY user_login ADD CONSTRAINT user_login_loginname_fkey2 FOREIGN KEY (loginname) REFERENCES user_account(loginname) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: id_provider; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
-- Is to be created manually, because not provided by Hibernate.
--

ALTER TABLE ONLY public.id_provider DROP CONSTRAINT id_provider_pkey;
DROP TABLE public.id_provider;

CREATE TABLE id_provider (type character varying(255) NOT NULL, current_id bigint);
ALTER TABLE id_provider OWNER TO postgres;

ALTER TABLE ONLY id_provider ADD CONSTRAINT id_provider_key PRIMARY KEY (type);
INSERT INTO id_provider(type, current_id) VALUES ('pure', '1000');

INSERT INTO organization (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, metadata, publicstatus, parentaffiliation_objectid) VALUES ('ou_persistent13', '2007-03-22 08:23:35.562', NULL, 'user_user42', '2011-06-08 08:08:11.522', NULL, 'user_user42', 'Max Planck Society', '{"city": "Hofgartenstra�e 8, 80539 Munich", "name": "Max Planck Society", "type": "institute", "startDate": "1948", "coordinates": {"altitude": 0.0, "latitude": 10.0, "longitude": 20.0}, "countryCode": "DE", "identifiers": [{"id": "http://www.mpg.de/"}], "descriptions": ["Die Max-Planck-Gesellschaft zur F�rderung der Wissenschaften e.V. ist eine unabh�ngige Forschungsorganisation, die Forschung vorrangig in eigenen Instituten f�rdert. Sie wurde am 26. Februar 1948 gegr�ndet � in Nachfolge der bereits 1911 errichteten Kaiser-Wilhelm-Gesellschaft zur F�rderung der Wissenschaften. Die derzeit 80 Max-Planck-Institute betreiben Grundlagenforschung in den Natur-, Bio-, Geistes- und Sozialwissenschaften im Dienste der Allgemeinheit.", "The Max Planck Society for the Advancement of Science is an independent, non-profit research organization that primarily promotes and supports research at its own institutes. It was founded on February 26, 1948, and is the successor organization to the Kaiser Wilhelm Society, which was established in 1911. The currently 80 Max Planck Institutes conduct basic research in the service of the general public in the natural sciences, life sciences, social sciences, and the humanities."], "alternativeNames": ["Max-Planck-Gesellschaft zur F�rderung der Wissenschaften e.V.", "MPS", "MPG"]}', 'OPENED', NULL);
INSERT INTO organization (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, metadata, publicstatus, parentaffiliation_objectid) VALUES ('ou_persistent25', '2007-03-22 08:23:35.562', NULL, 'user_user42', '2012-03-08 07:42:12.769', NULL, 'user_user42', 'Max Planck Digital Library', '{"city": "Amalienstr. 33, 80799 Munich", "name": "Max Planck Digital Library", "type": "institute", "startDate": "2007", "countryCode": "DE", "identifiers": [{}, {"id": "http://www.mpdl.mpg.de/"}, {}], "descriptions": ["", "The Max Planck Digital Library (MPDL) is a scientific service unit within the Max Planck Society, established in January 2007.\n\nThe MPDL provides services to help the MPS researchers manage their scientific information workflow. Such services comprise the provision of actual content and of technical solutions, but also the support to users by acting as a centre of competence and community facilitator in the domain of scientific information management.\n\nThis is achieved through close collaboration with the Max Planck Institutes and their libraries. The core activities of the MPDL lie in building up infrastructures and tools for publications and research data.\n\nA substantial task of the MPDL is to provide most effective access to scientific information and fostering the Open Access policy of the Max Planck Society.", "Die Max Planck Digital Library (MPDL) ist eine wissenschaftliche Serviceeinheit innerhalb der\nMax Planck Gesellschaft. Sie hat ihre Arbeit am 1. Januar 2007 aufgenommen.\n\nDie MPDL bietet den Forschern der Max-Planck-Gesellschaft Dienste an, die ihnen helfen, den wissenschaftlichen Informationsablauf zu organisieren. Diese Dienste beinhalten u.a. die Bereitstellung von Forschungsdaten und technischen L�sungen. Die MPDL unterst�tzt die Wissenschaftler als Kompetenzzentrum und Ratgeber im Bereich wissenschaftliches Informationsmanagement.\n\nEine wesentliche Aufgabe der MPDL ist es, einen optimalen Zugang zu wissenschaftlichen Informationen zu erm�glichen und die Max-Planck-Gesellschaft in ihrer Open Access Politik zu unterst�tzen.", ""], "alternativeNames": ["", "", "MPDL"]}', 'OPENED', 'ou_persistent13');
INSERT INTO organization (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, metadata, publicstatus, parentaffiliation_objectid) VALUES ('ou_40048', '2008-12-03 17:18:49.677', NULL, 'user_user42', '2011-06-08 15:55:37.892', 'roland', 'user_user42', 'Kaiser-Wilhelm-Gesellschaft','{"city": "Berlin", "name": "Kaiser-Wilhelm-Gesellschaft", "type": "society", "endDate": "1960", "startDate": "1911", "countryCode": "DE", "identifiers": [{"id": "http://www.mpg.de/178569/Kaiser-Wilhelm-Gesellschaft"}], "descriptions": ["The Kaiser Wilhelm Society for the Advancement of Science was founded in 1911 under the patronage of Kaiser Wilhelm II. It served as an umbrella organization for the Kaiser Wilhelm Institutes (KWI), which were responsible for the actual scientific activity. Following 1945, the KWI were gradualy adopted by the predecessing organization Max Planck Society.", "Die Kaiser-Wilhelm-Gesellschaft zur Förderung der Wissenschaften e.V. wurde 1911 unter der Schirmherrschaft Wilhelms II. gegründet. Sie war Trägerin der Kaiser-Wilhelm-Institute(KWI), die für die eigentliche Forschungsarbeit zuständig waren. Nach 1945 wurden die KWI schrittweise in die Nachfolge-Organisation Max-Planck-Gesellschaft überführt."], "alternativeNames": ["KWS", "KWG", "Kaiser Wilhelm Society"]}', 'CLOSED',  NULL);


INSERT INTO organization_predecessor (affiliationdbvo_objectid, predecessoraffiliations_objectid) VALUES ('ou_persistent13', 'ou_40048');

INSERT INTO context (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, allowedgenres, allowedsubjectclassifications, contactemail, description, state, workflow) VALUES ('ctx_persistent3', '2007-01-16 11:23:24.359', NULL, 'user_user42', '2016-01-25 11:59:31.873', NULL, 'user_user42', 'PubMan Test Collection', '["ARTICLE", "BOOK", "BOOK_ITEM", "PROCEEDINGS", "CONFERENCE_PAPER", "TALK_AT_EVENT", "CONFERENCE_REPORT", "POSTER", "COURSEWARE_LECTURE", "THESIS", "REPORT", "JOURNAL", "ISSUE", "SERIES", "OTHER", "EDITORIAL", "CONTRIBUTION_TO_HANDBOOK", "CONTRIBUTION_TO_FESTSCHRIFT", "CONTRIBUTION_TO_COMMENTARY", "CONTRIBUTION_TO_COLLECTED_EDITION", "BOOK_REVIEW", "CASE_STUDY", "CASE_NOTE", "ENCYCLOPEDIA", "COMMENTARY", "HANDBOOK", "COLLECTED_EDITION", "FESTSCHRIFT", "PATENT", "NEWSPAPER_ARTICLE", "PAPER", "MANUSCRIPT", "MANUAL", "OPINION", "MONOGRAPH", "NEWSPAPER", "MULTI_VOLUME", "MEETING_ABSTRACT", "FILM"]', '["DDC", "MPIPKS", "ISO639_3"]', 'pubman-support@gwdg.de', 'Sandbox collection for test purposes within the productive server.Please do not release any of the items stored in this context!', 'OPENED', 'STANDARD');
INSERT INTO context (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, allowedgenres, allowedsubjectclassifications, contactemail, description, state, workflow) VALUES ('ctx_2322554', '2016-07-25 08:54:42.848', NULL, 'user_user42', '2016-07-25 08:57:17.126', NULL, 'user_user42', 'Test_Context_Simple', '["JOURNAL", "ARTICLE", "ISSUE", "NEWSPAPER_ARTICLE", "BOOK", "BOOK_ITEM", "PROCEEDINGS", "CONFERENCE_PAPER", "MEETING_ABSTRACT", "CONFERENCE_REPORT", "POSTER", "REPORT", "TALK_AT_EVENT", "PAPER", "COURSEWARE_LECTURE", "THESIS", "SERIES", "MANUSCRIPT", "OTHER", "MANUAL", "EDITORIAL", "CONTRIBUTION_TO_HANDBOOK", "CONTRIBUTION_TO_ENCYCLOPEDIA", "CONTRIBUTION_TO_FESTSCHRIFT", "CONTRIBUTION_TO_COMMENTARY", "CONTRIBUTION_TO_COLLECTED_EDITION", "BOOK_REVIEW", "OPINION", "CASE_STUDY", "CASE_NOTE", "MONOGRAPH", "NEWSPAPER", "ENCYCLOPEDIA", "MULTI_VOLUME", "COMMENTARY", "HANDBOOK", "COLLECTED_EDITION", "FESTSCHRIFT", "PATENT", "FILM"]', '["DDC", "ISO639_3"]', '', 'Test_Context_Simple', 'OPENED', 'SIMPLE');

INSERT INTO user_account (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, active, email, grantlist, loginname, affiliation_objectid) VALUES ('user_3000056', '2017-05-31 13:36:45.703', 'roland', 'user_user42', '2017-05-31 13:36:45.703', 'roland', 'user_user42', 'Test Depositor', true, 'a@b.de', '[{"role": "DEPOSITOR", "objectRef": "ctx_2322554"}, {"role": "DEPOSITOR", "objectRef": "ctx_persistent3"}]', 'test_depositor', 'ou_persistent25');
INSERT INTO user_account (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, active, email, grantlist, loginname, affiliation_objectid) VALUES ('user_3000057', '2017-05-31 13:38:09.611', 'roland', 'user_user42', '2017-05-31 13:38:09.611', 'roland', 'user_user42', 'Test Moderator', true, 'b@b.de', '[{"role": "MODERATOR", "objectRef": "ctx_2322554"}, {"role": "MODERATOR", "objectRef": "ctx_persistent3"}]', 'test_moderator', 'ou_persistent25');
INSERT INTO user_account (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, active, email, grantlist, loginname, affiliation_objectid) VALUES ('user_3000165', '2017-06-02 09:56:08.234', 'roland', 'user_user42', '2017-06-02 09:56:08.816', 'roland', 'user_user42', 'Test Other Moderator', true, 'c@b.de', '[{"role": "MODERATOR", "objectRef": "ctx_2322554"}]', 'testCreateUserWithGrant', 'ou_persistent25');
INSERT INTO user_account (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, active, email, grantlist, loginname, affiliation_objectid) VALUES ('user_3000166', '2017-06-02 09:56:08.234', 'roland', 'user_user42', '2017-06-02 09:56:08.816', 'roland', 'user_user42', 'Test Moderator Deactivated', false, 'd@b.de', '[{"role": "MODERATOR", "objectRef": "ctx_2322554"}]', 'testDeactivated', 'ou_persistent25');
INSERT INTO user_account (objectid, creationdate, creator_name, creator_objectid, lastmodificationdate, modifier_name, modifier_objectid, name, active, email, grantlist, loginname, affiliation_objectid) VALUES ('user_user42', '2008-10-31 10:08:56.889', NULL, 'user_user42', '2010-09-21 09:15:07.051', NULL, 'user_user42', 'roland', true, 'roland@roland', '[{"role": "SYSADMIN", "grantType": "user-account"}]', 'admin', NULL);

INSERT INTO user_login (loginname, password) VALUES ('test_depositor', '$2a$10$V3cjl9UwaDT0IML9xBFQY.N/vL6jIdxm7DHwfyG5KWi40b21pJT0a');
INSERT INTO user_login (loginname, password) VALUES ('test_moderator', '$2a$10$V3cjl9UwaDT0IML9xBFQY.N/vL6jIdxm7DHwfyG5KWi40b21pJT0a');
INSERT INTO user_login (loginname, password) VALUES ('testCreateUserWithGrant', '$2a$10$G5rsfixnZQFICdYaCFKqzerkWSkaZPemUh7kgKW5meapNC4DeHbFu');
INSERT INTO user_login (loginname, password) VALUES ('testDeactivated', '$2a$10$V3cjl9UwaDT0IML9xBFQY.N/vL6jIdxm7DHwfyG5KWi40b21pJT0a');
INSERT INTO user_login (loginname, password) VALUES ('admin', '$2a$10$V3cjl9UwaDT0IML9xBFQY.N/vL6jIdxm7DHwfyG5KWi40b21pJT0a');



