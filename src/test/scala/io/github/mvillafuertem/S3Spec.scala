package io.github.mvillafuertem

import java.net.URI
import java.util.concurrent.CompletionException

import org.testcontainers.containers
import org.testcontainers.containers.wait.strategy.Wait
import software.amazon.awssdk.http.HttpStatusCode
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.{CreateBucketRequest, CreateBucketResponse, NoSuchKeyException}

import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.concurrent.Future

final class S3Spec extends LocalStackConfiguration {

  behavior of s"${this.getClass.getSimpleName}"

  it should "Create Bucket Request" in {

    // g i v e n
    val s3AsyncClientDefault = S3AsyncClient
      .builder()
      .region(Region.US_EAST_1)
      .endpointOverride(URI.create(AWS_LOCALSTACK_ENDPOINT))
      .build()

    val NEW_BUCKET_NAME = "new-bucket-test"
    val createBucketRequest = CreateBucketRequest
      .builder()
      .bucket(NEW_BUCKET_NAME)
      .build()

    // w h e n
    val createBucketResponse: Future[CreateBucketResponse] = for {
      headObjectResponse <- s3AsyncClientDefault
        .createBucket(createBucketRequest)
        .toScala
        .recover {
          case e: CompletionException if e.getCause.isInstanceOf[NoSuchKeyException] => throw e
          case e => throw e
        }
    } yield headObjectResponse

    // t h e n
    createBucketResponse.map { actual =>
      actual.sdkHttpResponse().statusCode() shouldBe HttpStatusCode.OK
    }

  }


  override var container: containers.DockerComposeContainer[_] = _

  override protected def beforeAll(): Unit = {
    container = dockerInfrastructure(Wait.forLogMessage(".*Starting mock S3 service.*\\n", 1))
    container.start()
  }

  override protected def afterAll(): Unit = container.stop()

}