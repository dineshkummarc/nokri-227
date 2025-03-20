package com.scriptsbundle.nokri.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.scriptsbundle.nokri.candidate.edit.models.CandidateAvailabilityList;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Glixen Technologies on 02/01/2018.
 */

public interface RestService {

    @POST("register/")
    Call<ResponseBody> postRegister(@Body JsonObject register);

    @POST("set_acount")
    Call<ResponseBody> postAccountTypleSelector(@Body JsonObject login, @HeaderMap Map<String, String> headers);

    @POST("login/")
    Call<ResponseBody> postLogin(@Body JsonObject login, @HeaderMap Map<String, String> headers);

    @POST("login/")
    Call<ResponseBody> postSocialLogin(@Body JsonObject login, @HeaderMap Map<String, String> headers);


    @POST("forgot/")
    Call<ResponseBody> postForgotPassword(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("canidate/dell_resumes/")
    Call<ResponseBody> postDeleteResume(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("candidate/external_apply")
    Call<ResponseBody> postExternalUrl(@Body JsonObject params, @HeaderMap Map<String, String> headers);

    @POST("canidate/dell_saved_job/")
    Call<ResponseBody> postDeleteJobs(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("canidate/dell_followed_companies/")
    Call<ResponseBody> postDeleteFollowedCompanies(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("canidate/del_portfolio/")
    Call<ResponseBody> postDeletePortfolio(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("candidate/portfolio_video_url")
    Call<ResponseBody> postYoutbe(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("candidate/update_education/")
    Call<ResponseBody> postCandidateEducation(@Body JsonArray reset, @HeaderMap Map<String, String> headers);

    @POST("candidate/update_profession/")
    Call<ResponseBody> postCandidateProfession(@Body JsonArray reset, @HeaderMap Map<String, String> headers);

    @POST("candidate/update_certifications")
    Call<ResponseBody> postCandidateCertification(@Body JsonArray reset, @HeaderMap Map<String, String> headers);

    @POST("canidate/update_personal_info")
    Call<ResponseBody> postCandidatePersonalInfo(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("canidate/update_social_link")
    Call<ResponseBody> postCandidateSocialLinks(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("canidate/update_location")
    Call<ResponseBody> postCandidateLocation(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("user_contact")
    Call<ResponseBody> postContactUS(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("canidate/update_skills")
    Call<ResponseBody> postCandidateSkills(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("candidate/saving_jobs")
    Call<ResponseBody> bookmarkJob(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST("candidate/cover")
    Call<ResponseBody> postUploadCover(@Part MultipartBody.Part image, @HeaderMap Map<String, String> headers);

    @POST("candidate/applying_jobs")
    Call<ResponseBody> getApplyJobPopup(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("candidate/sending_resume")
    Call<ResponseBody> postApplyJob(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("home")
    Call<ResponseBody> sendFirebaseId(@Body JsonObject reset,@HeaderMap Map<String, String> headers);

    @POST("candidate/sending_email_resume")
    Call<ResponseBody> postApplyJobWithExternalEmail(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("candidate/following_companies")
    Call<ResponseBody> postFollowCompany(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("canidate/applied_jobs")
    Call<ResponseBody> getAppliedJobs(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("candidate/jobs_matched")
    Call<ResponseBody> getJobsForYou(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("canidate/saved_jobs")
    Call<ResponseBody> getSavedJobs(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("candidate/aplly_linkedin")
    Call<ResponseBody> applyJobLinkedin(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("canidate/followed_companies")
    Call<ResponseBody> getFollowedCompaniesLoadMore(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST("candidate/dp")
    Call<ResponseBody> postUploadProfileImage(@Part MultipartBody.Part image, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST("candidate/cand_resumes_video")
    Call<ResponseBody> postUploadResume(@Part MultipartBody.Part image, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST("canidate/resume_upload")
    Call<ResponseBody> postUploadResumeNormal(@Part MultipartBody.Part image, @HeaderMap Map<String, String> headers);


    @Multipart
    @POST("job_post/upload_attach/")
    Call<ResponseBody> postUploadDynamicAttachments(@Part MultipartBody.Part file, @Part("job_id") RequestBody requestBody,@HeaderMap Map<String,String> headers);

    @POST("job_post/dell_attach/")
    Call<ResponseBody> postDeleteAttachment(@Body JsonObject params,@HeaderMap Map<String,String> headers);

    @Multipart
    @POST("canidate/portfolio_upload")
    Call<ResponseBody> postUploadPortfolio(@Part MultipartBody.Part parts[], @HeaderMap Map<String, String> headers);

    @Multipart
    @POST("canidate/portfolio_upload")
    Call<ResponseBody> postUploadPortfolio(@Part List<MultipartBody.Part> parts, @HeaderMap Map<String, String> headers);

    //Get
    @GET("set_acount")
    Call<ResponseBody> getAccoutTypeSelector(@HeaderMap Map<String, String> headers);

    @GET("register/")
    Call<ResponseBody> getRegisterSettings();

    @GET("login/")
    Call<ResponseBody> getLoginSetting();

    @GET("forgot/")
    Call<ResponseBody> getForgotPassword();

    @GET("canidate/dashboard")
    Call<ResponseBody> getCandidateDashboard(@HeaderMap Map<String, String> headers);

    @GET("candidate/dashboard_tabs")
    Call<ResponseBody> getCandidateSettings(@HeaderMap Map<String, String> headers);

    @GET("canidate/resumes_list")
    Call<ResponseBody> getCandidateResumeList(@HeaderMap Map<String, String> headers);

    @GET("canidate/followed_companies")
    Call<ResponseBody> getFollowedCompanies(@HeaderMap Map<String, String> headers);

    @GET("canidate/profile")
    Call<ResponseBody> getCandidateProfile(@HeaderMap Map<String, String> headers);

    @GET("canidate/education")
    Call<ResponseBody> getCandidateEducation(@HeaderMap Map<String, String> headers);

    @GET("canidate/profession")
    Call<ResponseBody> getCandidateProfession(@HeaderMap Map<String, String> headers);

    @GET("canidate/certifications")
    Call<ResponseBody> getCandidateCerification(@HeaderMap Map<String, String> headers);

    @GET("canidate/portfolio")
    Call<ResponseBody> getCandidatePortfolio(@HeaderMap Map<String, String> headers);

    @GET
    @Streaming
    Call<ResponseBody> downloadFile(@Url String url);

    @GET("canidate/education")
    Call<ResponseBody> getCandidateEducationForEditEducation(@HeaderMap Map<String, String> headers);

    @GET("canidate/education/add_more")
    Call<ResponseBody> getCandidateEducationAddMore(@HeaderMap Map<String, String> headers);

    @GET("canidate/profession")
    Call<ResponseBody> getCandidateProfessionForEditProfession(@HeaderMap Map<String, String> headers);

    @GET("canidate/profession/add_more")
    Call<ResponseBody> getCandidateProfessionAddMore(@HeaderMap Map<String, String> headers);

    @GET("canidate/certifications")
    Call<ResponseBody> getCandidateCertificationForEditCertification(@HeaderMap Map<String, String> headers);

    @GET("canidate/certifications/add_more")
    Call<ResponseBody> getCandidateCertificationAddMore(@HeaderMap Map<String, String> headers);

    @GET("canidate/update_personal_info")
    Call<ResponseBody> getCandidatePersonalinfo(@HeaderMap Map<String, String> headers);

    @GET("canidate/update_social_link")
    Call<ResponseBody> getCandidateSocialLinks(@HeaderMap Map<String, String> headers);

    @GET("canidate/update_skills")
    Call<ResponseBody> getCandidateSkills(@HeaderMap Map<String, String> headers);

    @GET("canidate/update_location")
    Call<ResponseBody> getCandidateLocation(@HeaderMap Map<String, String> headers);


    // Employeer
    @GET("employer/get_profile")
    Call<ResponseBody> getEmployeerDashboard(@HeaderMap Map<String, String> headers);

    @GET("employer/update_personal_info")
    Call<ResponseBody> getEmployeerInfo(@HeaderMap Map<String, String> headers);

    @GET("employer/cover_dp")
    Call<ResponseBody> getEmployeerProfile(@HeaderMap Map<String, String> headers);

    @GET("employer/update_skills")
    Call<ResponseBody> getEmployeerSkills(@HeaderMap Map<String, String> headers);

    @GET("employer/update_social_link")
    Call<ResponseBody> getEmployeerSocialLinks(@HeaderMap Map<String, String> headers);

    @GET("employer/update_location")
    Call<ResponseBody> getEmployeerLocation(@HeaderMap Map<String, String> headers);

    @GET("employer/company_followers")
    Call<ResponseBody> getEmployeerFollowedCompanies(@HeaderMap Map<String, String> headers);

    @GET("employer/view_template")
    Call<ResponseBody> getEmployeerEmailList(@HeaderMap Map<String, String> headers);

    @GET("employer/email_template")
    Call<ResponseBody> getEmployeerEmailLabels(@HeaderMap Map<String, String> headers);

    @GET("employer/active_jobs")
    Call<ResponseBody> getActiveJobs(@HeaderMap Map<String, String> headers);

    @GET("employer/inactive_jobs")
    Call<ResponseBody> getInActiveJobs(@HeaderMap Map<String, String> headers);

    @GET("employer/get_templates")
    Call<ResponseBody> getTemplatesPopup(@HeaderMap Map<String, String> headers);

    @GET("employer/job_post")
    Call<ResponseBody> getPostJob(@HeaderMap Map<String, String> headers);

    @GET("packages")
    Call<ResponseBody> getPackages(@HeaderMap Map<String, String> headers);

    @GET("cand_packages")
    Call<ResponseBody> getCandidatePackages(@HeaderMap Map<String, String> headers);

    @GET("payment/complete")
    Call<ResponseBody> getThankYou(@HeaderMap Map<String, String> headers);

    @GET("payment/card")
    Call<ResponseBody> getStripe(@HeaderMap Map<String, String> headers);

    @GET("employer/package_details")
    Call<ResponseBody> getPackageDetail(@HeaderMap Map<String, String> headers);
    //Post

    @GET("candidate/package_details")
    Call<ResponseBody> getCandidatePackageDetail(@HeaderMap Map<String, String> headers);
    //Post

    @POST("employer/company_followers")
    Call<ResponseBody> companyFollowersLoadMore(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/update_personal_info")
    Call<ResponseBody> postEmployeePersonalInfo(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST("employer/cover")
    Call<ResponseBody> postUploadEmployeerCover(@Part MultipartBody.Part image, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST("employer/logo")
    Call<ResponseBody> postUploadEmployeerProfileImage(@Part MultipartBody.Part image, @HeaderMap Map<String, String> headers);

    @POST("employer/update_skills")
    Call<ResponseBody> postEmployeerSkills(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/update_social_link")
    Call<ResponseBody> postEmployeerSocialLinks(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/update_location")
    Call<ResponseBody> postEmployeerLocation(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/company_del_followers")
    Call<ResponseBody> postDeleteFollowedEmployees(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/email_template")
    Call<ResponseBody> postEmailTeplate(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/del_email_temp")
    Call<ResponseBody> postDeleteTemplate(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/active_jobs")
    Call<ResponseBody> filterActiveJobs(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/inactive_this_job")
    Call<ResponseBody> inActiveJob(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/bump_this_job")
    Call<ResponseBody> bumpUpJob(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/active_this_job")
    Call<ResponseBody> activeJob(@Body JsonObject reset, @HeaderMap Map<String,  String> headers);

    @POST("employer/resumes_recieved")
    Call<ResponseBody> getReceivedResumes(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/del_this_job")
    Call<ResponseBody> postDeleteJob(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/resumes_recieved")
    Call<ResponseBody> filterResumeWithName(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/resumes_recieved")
    Call<ResponseBody> filterReceivedResume(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/template_load")
    Call<ResponseBody> filterEmailTemplates(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/sending_email")
    Call<ResponseBody> postSendEail(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("candidate/public_profile")
    Call<ResponseBody> getCandidatePublicProfile(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/active_jobs")
    Call<ResponseBody> getActiveJobsAddMore(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/company_followers")
    Call<ResponseBody> getEmployeerFollowedCompaniesByName(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("child_cats")
    Call<ResponseBody> getSubFields(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("city_state")
    Call<ResponseBody> getCountryCityState(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/job_post")
    Call<ResponseBody> postJob(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/job_update")
    Call<ResponseBody> editPostJob(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("payment")
    Call<ResponseBody> postPackages(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("cand_payment")
    Call<ResponseBody> postCandidatePackages(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("payment")
    Call<ResponseBody> postStripe(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    // Public
    //Post
    @POST("view_job")
    Call<ResponseBody> getJobDetail(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("all_jobs")
    Call<ResponseBody> getAllJobsAddMore(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("all_jobs")
    Call<ResponseBody> filterAllJobs(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("employer/public_profile")
    Call<ResponseBody> getEmployeerPublicProfile(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("posts")
    Call<ResponseBody> getBlog(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("home_blog")
    Call<ResponseBody> getHomeBlog(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("contact/us")
    Call<ResponseBody> postContactUs(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("posts/detail")
    Call<ResponseBody> getBlogDetails(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("posts/comments")
    Call<ResponseBody> postBlogComment(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("job_search")
    Call<ResponseBody> postFilters(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("premium_jobs")
    Call<ResponseBody> getFeaturedJobs(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("deactivate_my_acount")
    Call<ResponseBody> deleteAccount(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("generateChecksum.php")
    Call<ResponseBody> getChecksum(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    //Get
    @GET("job_search")
    Call<ResponseBody> getFilters(@HeaderMap Map<String, String> headers);

    @GET("candidate_search")
    Call<ResponseBody> getCandidateSearchFilters(@HeaderMap Map<String, String> headers);

    @POST("candidate_search")
    Call<ResponseBody> getFilteredCandidates(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @GET("home")
    Call<ResponseBody> getHome(@HeaderMap Map<String, String> headers);


    @GET("home_full")
    Call<ResponseBody> getHomeFull(@HeaderMap Map<String, String> headers);

    @GET("home2")
    Call<ResponseBody> getSecondHome(@HeaderMap Map<String, String> headers);

    @GET("login_activity")
    Call<ResponseBody> getMainActivity();

    @GET("faqs")
    Call<ResponseBody> getFaq(@HeaderMap Map<String, String> headers);

    @GET("reset_password")
    Call<ResponseBody> getResetPassword(@HeaderMap Map<String, String> headers);

    @POST("reset_password")
    Call<ResponseBody> postResetPassword(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @POST("feedback")
    Call<ResponseBody> postFeedback(@Body JsonObject rest, @HeaderMap Map<String, String> headers);

    @POST("employer/job_post/dynamic_fields/")
    Call<ResponseBody> postGetDynamicFields(
            @Body JsonObject getDynamicFields,
            @HeaderMap Map<String, String> headers
    );

    @POST("employer/saving_cand_resumes")
    Call<ResponseBody> saveResume(@Body JsonObject rest, @HeaderMap Map<String, String> headers);


    @GET("employer/saved_resumes")
    Call<ResponseBody> getSavedResumes(@HeaderMap Map<String, String> headers);

    @POST("employer/removing_cand_resumes")
    Call<ResponseBody> removeSavedResumes(@Body JsonObject params,@HeaderMap Map<String, String> headers);


    @POST("job_alert_subsucription")
    Call<ResponseBody> createJobAlert(@Body JsonObject params,@HeaderMap Map<String, String> headers);

    @Multipart
    @POST("canidate/uploding_resume")
    Call<ResponseBody> uploadResumeWithoutLogin(@Part MultipartBody.Part image,@HeaderMap Map<String, String> headers);


    @GET("candidate/job_alerts")
    Call<ResponseBody> getSavedJobAlerts(@HeaderMap Map<String, String> headers);


    @POST("candidate/delete_job_alert")
    Call<ResponseBody> deleteJobAlert(@Body JsonObject params,@HeaderMap Map<String, String> headers);

    @POST("canidate/cand_save_video")
    Call<ResponseBody> saveResumeVideo(@Body JsonObject params,@HeaderMap Map<String, String> headers);

    @POST("employer/del_this_candidate")
    Call<ResponseBody> deleteCandidateResume(@Body JsonObject params,@HeaderMap Map<String, String> headers);

    @GET("employer/jobs_matched_resumes")
    Call<ResponseBody> getMatchedResumes(@HeaderMap Map<String, String> headers);


    @GET("candidate/skill_match_notification")
    Call<ResponseBody> getCandidateNotifications(@HeaderMap Map<String, String> headers);

    @POST("employer/single_job_matched_resume")
    Call<ResponseBody> getMatchedCandidates(@Body JsonObject params,@HeaderMap Map<String, String> headers);


    @GET("employer/portfolio")
    Call<ResponseBody> getEmployerPortfolio(@HeaderMap Map<String, String> headers);

    @POST("employer/del_portfolio")
    Call<ResponseBody> postDeleteEmployerPortfolio(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @Multipart
    @POST("employer/portfolio_upload")
    Call<ResponseBody> postUploadEmployerPortfolio(@Part List<MultipartBody.Part> parts, @HeaderMap Map<String, String> headers);



    @POST("employer/portfolio_video_url")
    Call<ResponseBody> postEmployerYoutube(@Body JsonObject reset, @HeaderMap Map<String, String> headers);

    @GET("employer_search")
    Call<ResponseBody> getEmployerSearchFilters(@HeaderMap Map<String, String> headers);

    @POST("employer_search")
    Call<ResponseBody> postFilteredEmployerSearch(@Body JsonObject reset, @HeaderMap Map<String, String> headers);


    @GET("canidate/comp_profile")
    Call<ResponseBody> getCandidateEditDetails(@HeaderMap Map<String, String> headers);

    @GET("employer/comp_profile")
    Call<ResponseBody> getCompanyEditDetails(@HeaderMap Map<String, String> headers);

    @GET("canidate/schedule_hours")
    Call<CandidateAvailabilityList> getCandidateAvailability(@HeaderMap Map<String, String> headers);

    @POST("candidate/get_ratings")
    Call<ResponseBody> getCandidateRatings(@Body JsonObject params,@HeaderMap Map<String, String> headers);

    @POST("employer/get_ratings")
    Call<ResponseBody> getEmployerRatings(@Body JsonObject params,@HeaderMap Map<String, String> headers);

    @POST("candidate/schedule_hours_post")
    Call<ResponseBody> postScheduledHours(@Body JsonObject params, @HeaderMap Map<String, String> headers);

    @POST("candidate/post_ratings")
    Call<ResponseBody> postRating(@Body JsonObject params, @HeaderMap Map<String, String> headers);

    @POST("employer/post_ratings")
    Call<ResponseBody> postEmployerRating(@Body JsonObject params, @HeaderMap Map<String, String> headers);

    @POST("user_post_reply")
    Call<ResponseBody> postReply(@Body JsonObject params, @HeaderMap Map<String, String> headers);


}




