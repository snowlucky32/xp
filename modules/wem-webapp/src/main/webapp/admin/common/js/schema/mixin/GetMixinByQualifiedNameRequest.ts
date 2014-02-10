module api.schema.mixin {

    export class GetMixinByQualifiedNameRequest extends MixinResourceRequest<api.schema.mixin.json.MixinJson> {

        private name:MixinName;

        constructor(name:MixinName) {
            super();
            super.setMethod("GET");
            this.name = name;
        }

        getParams():Object {
            return {
                name: this.name.toString()
            };
        }

        getRequestPath():api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): Q.Promise<api.schema.mixin.Mixin> {

            var deferred = Q.defer<api.schema.mixin.Mixin>();

            this.send().then((response: api.rest.JsonResponse<api.schema.mixin.json.MixinJson>) => {
                deferred.resolve(this.fromJsonToMixin(response.getResult()));
            }).catch((response: api.rest.RequestError) => {
                    deferred.reject(null);
                }).done();

            return deferred.promise;
        }
    }
}