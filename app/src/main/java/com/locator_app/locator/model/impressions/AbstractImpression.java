package com.locator_app.locator.model.impressions;


import com.locator_app.locator.apiservice.Api;

public abstract class AbstractImpression {

    private Impression impression;
    private ImpressionType type;

    public String getUserId() {
        return impression.userId;
    }

    public String getId() {
        return impression.id;
    }

    public String getCreateDate() {
        return impression.createDate;
    }

    public ImpressionType type() {
        return type;
    }

    public enum ImpressionType {
        VIDEO {
            public AbstractImpression create(Impression impression) {
                return new VideoImpression(Api.serverUrl + impression.data);
            }
        },
        IMAGE {
            public AbstractImpression create(Impression impression) {
                return new ImageImpression(Api.serverUrl + impression.data);
            }
        },
        TEXT {
            public AbstractImpression create(Impression impression) {
                return new TextImpression(impression.data);
            }
        };

        public AbstractImpression create(final Impression unused) {
            return null;
        }
    }

    public static AbstractImpression createImpression(Impression impression) {
        ImpressionType type = ImpressionType.valueOf(impression.type.toUpperCase());
        AbstractImpression instance = type.create(impression);
        if (instance != null) {
            instance.type = type;
            instance.impression = impression;
        }
        return instance;
    }

}
