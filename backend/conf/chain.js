{
    "default" : {
        "ps" : [
              "org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor",
              "org.nutz.mvc.impl.processor.EncodingProcessor",
              "org.nutz.mvc.impl.processor.ModuleProcessor",
              "org.nutz.mvc.impl.processor.ActionFiltersProcessor",
              "org.nutz.mvc.impl.processor.AdaptorProcessor",
              "org.nutz.mvc.impl.processor.MethodInvokeProcessor",
              "ioc:corsProcessor", // Insert CorsProcessor to enable CORS access
              "org.nutz.mvc.impl.processor.ViewProcessor"
          ],
        "error" : 'org.nutz.mvc.impl.processor.FailProcessor'
    }
}
