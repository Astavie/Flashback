package com.moulberry.flashback.keyframe.impl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.moulberry.flashback.Utils;
import com.moulberry.flashback.editor.ui.ImGuiHelper;
import com.moulberry.flashback.keyframe.Keyframe;
import com.moulberry.flashback.keyframe.handler.KeyframeHandler;
import com.moulberry.flashback.keyframe.interpolation.InterpolationType;
import imgui.ImGui;
import imgui.type.ImString;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class TimelapseKeyframe extends Keyframe {

    public int ticks;

    private final ImString timelapseKeyframeInput;

    public TimelapseKeyframe(int ticks) {
        this.ticks = ticks;
        this.timelapseKeyframeInput = ImGuiHelper.createResizableImString(Utils.timeToString(this.ticks));
        this.timelapseKeyframeInput.inputData.allowedChars = "0123456789tsmh";
        this.interpolationType(InterpolationType.LINEAR);
    }

    @Override
    public Keyframe copy() {
        return new TimelapseKeyframe(this.ticks);
    }

    @Override
    public InterpolationType interpolationType() {
        return InterpolationType.LINEAR;
    }

    @Override
    public void renderEditKeyframe(Consumer<Consumer<Keyframe>> update) {
        ImGui.setNextItemWidth(160);
        ImGui.inputText("Time", timelapseKeyframeInput);
        if (ImGui.isItemDeactivatedAfterEdit()) {
            int ticks = Utils.stringToTime(timelapseKeyframeInput.get());
            if (this.ticks != ticks) {
                update.accept(keyframe -> ((TimelapseKeyframe)keyframe).ticks = ticks);
            }
        }
    }

    @Override
    public void apply(KeyframeHandler keyframeHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyInterpolated(KeyframeHandler keyframeHandler, Keyframe otherGeneric, float amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyInterpolatedSmooth(KeyframeHandler keyframeHandler, Keyframe p1, Keyframe p2, Keyframe p3, float t0, float t1, float t2, float t3, float amount, float lerpAmount, boolean lerpFromRight) {
        throw new UnsupportedOperationException();
    }

    public static class TypeAdapter implements JsonSerializer<TimelapseKeyframe>, JsonDeserializer<TimelapseKeyframe> {
        @Override
        public TimelapseKeyframe deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int ticks = jsonObject.get("ticks").getAsInt();
            return new TimelapseKeyframe(ticks);
        }

        @Override
        public JsonElement serialize(TimelapseKeyframe src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ticks", src.ticks);
            jsonObject.addProperty("type", "timelapse");
            return jsonObject;
        }
    }
}