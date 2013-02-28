/*
 * Sonar Sonargraph Plugin
 * Copyright (C) 2009, 2010, 2011 hello2morrow GmbH
 * mailto: info AT hello2morrow DOT com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hello2morrow.sonarplugin.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;

import com.hello2morrow.sonarplugin.foundation.IReportReader;
import com.hello2morrow.sonarplugin.foundation.ReportReaderMock;
import com.hello2morrow.sonarplugin.foundation.TestHelper;
import com.hello2morrow.sonarplugin.metric.SonargraphSimpleMetrics;

/**
 * @author Ingmar
 * 
 */
public class SonargraphSensorTest {

  private static RulesProfile rulesProfile;
  private static SensorContext sensorContext;
  private SonargraphSensor sensor;
  private static final String REPORT = "src/test/resources/sonargraph-sonar-report.xml";

  @BeforeClass
  public static void initialize() {
    rulesProfile = TestHelper.initRulesProfile();
    sensorContext = TestHelper.initSensorContext();
  }

  @Before
  public void initSensor() {
    IReportReader reader = new ReportReaderMock(REPORT);
    this.sensor = new SonargraphSensor(rulesProfile, reader, sensorContext);
  }

  @Test
  public void testAnalyseRootParentProject() {
    Project rootProject = new Project("hello2morrow:AlarmClock");
    Project module = new Project("module");
    module.setParent(rootProject);
    SensorContext context = mock(SensorContext.class);
    when(context.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES)).thenReturn(null);

    sensor.analyse(null, context);
    assertNull(context.getMeasure(SonargraphSimpleMetrics.INTERNAL_PACKAGES));
  }

  @Test
  public void testAnalyse() {
    Project project = new Project("hello2morrow:AlarmClock", "", "AlarmClock");
    project.setConfiguration(TestHelper.initConfig());
    sensor.analyse(project, sensorContext);
    double value = sensorContext.getMeasure(SonargraphSimpleMetrics.WORKSPACE_WARNINGS).getValue().doubleValue();
    assertEquals(0.0, value, 0.01);
  }

  @Test
  public void testShouldExecuteOnProject() {
    Project project = new Project("hello2morrow:AlarmClock", "", "AlarmClock");
    project.setLanguage(Java.INSTANCE);
    assertTrue(sensor.shouldExecuteOnProject(project));

    Project module = new Project("hello2morrow:Foundation", "", "Foundation");
    module.setLanguage(Java.INSTANCE);
    module.setParent(project);
    assertFalse(sensor.shouldExecuteOnProject(project));
    assertTrue(sensor.shouldExecuteOnProject(module));
  }

  public void testHandleDuplicateCodeBlocks() {
    // sensor.handleDuplicateCodeBlocks(warningGroup, buildUnitName, Rule.create(repositoryKey, key, name)());
  }
}
