package hybrid.api.shader;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import hybrid.api.rendering.ScreenBounds;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2f;

import static net.minecraft.client.gl.RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET;

public class HueShader {

    public static final RenderPipeline.Snippet GUI_SNIPPET =
            RenderPipeline.builder(TRANSFORMS_AND_PROJECTION_SNIPPET)
                          .withVertexShader(Identifier.of("hybrid-api", "core/hue")).withFragmentShader(Identifier.of("hybrid-api", "core/hue"))
                          .withBlend(BlendFunction.TRANSLUCENT).
                          withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
                          .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).
                          buildSnippet()
            ;

    public static final RenderPipeline GUI = RenderPipelines.register(RenderPipeline.builder(GUI_SNIPPET).withLocation("pipeline/hue").build());

    public static void fill(DrawContext context, ScreenBounds bounds) {
        context.state.addSimpleElement(new ColoredQuadGuiElementRenderState(GUI, TextureSetup.empty(),
                new Matrix3x2f(context.getMatrices()), bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), -1, -1, context.scissorStack.peekLast()));
    }
}