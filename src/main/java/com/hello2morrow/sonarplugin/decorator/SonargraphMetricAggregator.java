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

package com.hello2morrow.sonarplugin.decorator;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.AbstractSumChildrenDecorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;

import com.hello2morrow.sonarplugin.AlertDecorator;
import com.hello2morrow.sonarplugin.DecoratorProjectContext;
import com.hello2morrow.sonarplugin.Utilities;
import com.hello2morrow.sonarplugin.metric.SonargraphBuildUnitMetrics;

public final class SonargraphMetricAggregator extends AbstractSumChildrenDecorator {

  private static final Logger LOG = LoggerFactory.getLogger(SonargraphMetricAggregator.class);

  @Override
  @DependedUpon
  public List<Metric> generatesMetrics() {
    
    /* Some of these metrics could also be directly retrieved from the "overview" section
     * of the generated report, e.g. internal types, instructions... 
     * But it is safe to add up the values from the different build units being analyzed.
     */
    return Arrays.asList(SonargraphBuildUnitMetrics.CYCLICITY,  
        SonargraphBuildUnitMetrics.INSTRUCTIONS, 
        SonargraphBuildUnitMetrics.UNASSIGNED_TYPES, SonargraphBuildUnitMetrics.EROSION_REFS, 
        SonargraphBuildUnitMetrics.EROSION_TYPES, SonargraphBuildUnitMetrics.EROSION_COST,
        SonargraphBuildUnitMetrics.EROSION_INDEX, SonargraphBuildUnitMetrics.VIOLATING_TYPES, 
        SonargraphBuildUnitMetrics.INTERNAL_TYPES, SonargraphBuildUnitMetrics.VIOLATING_DEPENDENCIES,
        SonargraphBuildUnitMetrics.TYPE_DEPENDENCIES, SonargraphBuildUnitMetrics.JAVA_FILES, 
        SonargraphBuildUnitMetrics.TASKS, SonargraphBuildUnitMetrics.TASK_REFS, 
        SonargraphBuildUnitMetrics.THRESHOLD_WARNINGS, SonargraphBuildUnitMetrics.DUPLICATE_WARNINGS,
        SonargraphBuildUnitMetrics.IGNORED_VIOLATONS, SonargraphBuildUnitMetrics.IGNORED_WARNINGS,
        SonargraphBuildUnitMetrics.INTERNAL_PACKAGES, SonargraphBuildUnitMetrics.CYCLIC_PACKAGES);
  }

  @Override
  protected boolean shouldSaveZeroIfNoChildMeasures() {
    return false;
  }

  @Override
  public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context) {
    if ( !shouldDecorateResource(resource)) {
      return;
    }
    if (!Utilities.isAggregationProject(context)) {
      return;
    }
    super.decorate(resource, context);

    AlertDecorator.setAlertLevels(new DecoratorProjectContext(context));
  }

  @Override
  public boolean shouldDecorateResource(@SuppressWarnings("rawtypes") Resource resource) {
    LOG.debug("Checking for resource type: " + resource.getQualifier());
    return Arrays.asList(Qualifiers.PROJECT, Qualifiers.MODULE, Qualifiers.VIEW, Qualifiers.SUBVIEW).contains(
        resource.getQualifier());
  }
}