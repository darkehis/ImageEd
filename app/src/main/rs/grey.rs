#pragma version(1)
#pragma rs java_package_name(com.example.jonathan.imageed)






void root (const uchar4* in,uchar4* out,uint32_t x,uint32_t y){

  const float4 inF = rsUnpackColor8888(*in);
  float g = 0.3*inF.r + 0.59*inF.g + 0.11*inF.b;
  float4 outF = {g,g,g,255};
  *out =  rsPackColorTo8888(outF);


}
