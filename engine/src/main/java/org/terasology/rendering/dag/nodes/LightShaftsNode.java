/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.rendering.dag.nodes;

import org.terasology.config.Config;
import org.terasology.config.RenderingConfig;
import org.terasology.monitoring.PerformanceMonitor;
import org.terasology.registry.In;
import org.terasology.rendering.assets.material.Material;
import org.terasology.rendering.dag.AbstractNode;
import org.terasology.rendering.opengl.FBO;
import org.terasology.rendering.opengl.FrameBuffersManager;
import org.terasology.rendering.world.WorldRenderer;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.terasology.rendering.opengl.OpenGLUtils.bindDisplay;
import static org.terasology.rendering.opengl.OpenGLUtils.setViewportToSizeOf;
import static org.terasology.rendering.opengl.OpenGLUtils.renderFullscreenQuad;

/**
 * TODO: Add diagram of this node
 */
public class LightShaftsNode extends AbstractNode {

    @In
    private Config config;

    @In
    private FrameBuffersManager frameBuffersManager;

    @In
    private WorldRenderer worldRenderer;

    private RenderingConfig renderingConfig;
    private Material lightShaftsShader;
    private FBO lightShaftsFBO;
    private FBO sceneOpaque;

    @Override
    public void initialise() {
        renderingConfig = config.getRendering();
        lightShaftsShader = worldRenderer.getMaterial("engine:prog.lightshaft"); // TODO: rename shader to lightShafts
    }

    @Override
    public void process() {
        if (renderingConfig.isLightShafts()) {
            PerformanceMonitor.startActivity("rendering/lightShafts");
            lightShaftsFBO = frameBuffersManager.getFBO("lightShafts");
            sceneOpaque = frameBuffersManager.getFBO("sceneOpaque");

            lightShaftsShader.enable();
            // TODO: verify what the inputs are
            lightShaftsFBO.bind();

            setViewportToSizeOf(lightShaftsFBO);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // TODO: verify this is necessary

            renderFullscreenQuad();

            bindDisplay();     // TODO: verify this is necessary
            setViewportToSizeOf(sceneOpaque);    // TODO: verify this is necessary

            PerformanceMonitor.endActivity();
        }
    }
}
