package ws.furrify.posts.post;

import org.springframework.cloud.openfeign.FeignClient;
import ws.furrify.posts.tag.TagServiceClient;

@FeignClient(name = "TAGS-SERVICE")
interface TagServiceClientImpl extends TagServiceClient {
}
