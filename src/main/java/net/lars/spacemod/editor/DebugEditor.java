package net.lars.spacemod.editor;

import foundry.veil.api.client.editor.SingleWindowEditor;
import imgui.ImGui;
import imgui.type.ImFloat;
import net.lars.spacemod.shader.BloomShaderManager;
import net.lars.spacemod.shader.FlameShaderManager;

public class DebugEditor extends SingleWindowEditor {

    ImFloat uniform_diamond_multiplier = new ImFloat(FlameShaderManager.u_diamondMult);
    ImFloat uniform_overall_multiplier = new ImFloat(FlameShaderManager.u_overallMult);
    ImFloat uniform_flame_multiplier = new ImFloat(FlameShaderManager.u_flameMult);
    ImFloat uniform_flame_repeat = new ImFloat(FlameShaderManager.u_repeat);
    ImFloat uniform_flame_offset = new ImFloat(FlameShaderManager.u_offset);

    ImFloat uniform_blur_offset = new ImFloat(BloomShaderManager.blurOffset);

    @Override
    protected void renderComponents() {
        ImGui.inputFloat("Diamond Intensity",uniform_diamond_multiplier,0.05f);
        ImGui.inputFloat("Flame Intensity",uniform_flame_multiplier,0.05f);
        ImGui.inputFloat("Overall Intensity",uniform_overall_multiplier,0.05f);
        ImGui.inputFloat("Flame offset",uniform_flame_offset,0.05f);
        ImGui.inputFloat("Flame repeat",uniform_flame_repeat,0.05f);
        ImGui.inputFloat("Blur offset",uniform_blur_offset,0.05f);

        FlameShaderManager.u_diamondMult = uniform_diamond_multiplier.get();
        FlameShaderManager.u_overallMult = uniform_overall_multiplier.get();
        FlameShaderManager.u_flameMult   = uniform_flame_multiplier.get();
        FlameShaderManager.u_offset = uniform_flame_offset.get();
        FlameShaderManager.u_repeat = uniform_flame_repeat.get();

        BloomShaderManager.blurOffset = uniform_blur_offset.get();
    }

    @Override
    public String getDisplayName() {
        return "Shader Debug Window";
    }
}
